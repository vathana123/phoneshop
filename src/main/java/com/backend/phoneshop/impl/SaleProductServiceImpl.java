package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.dto.SaleDetailDto;
import com.backend.phoneshop.dto.SaleProductDto;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.SaleDetail;
import com.backend.phoneshop.entity.SaleProduct;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.exception.ValidationException;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.mapper.SaleDetailMapper;
import com.backend.phoneshop.mapper.SaleProductMapper;
import com.backend.phoneshop.repository.ProductRepository;
import com.backend.phoneshop.repository.SaleDetailRepository;
import com.backend.phoneshop.repository.SaleProductRepository;
import com.backend.phoneshop.service.SaleProductService;
import com.backend.phoneshop.specification.SaleProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleProductServiceImpl implements SaleProductService {

    private static final String UPDATE_NOT_ALLOWED_MESSAGE = "Updating sale products is not supported.";

    private final SaleProductRepository repository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final SaleProductMapper mapper;
    private final SaleDetailMapper saleDetailMapper;

    @Override
    public PageResponse<SaleProductDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<SaleProduct> page = repository.findAll(SaleProductSpecification.builder().filters(filters).build(), pageable);
        List<SaleProduct> saleProducts = page.getContent();
        if (saleProducts.isEmpty()) {
            return PageResponseMapper.toPageResponse(page, mapper::toDto);
        }

        Map<Long, List<SaleDetail>> saleProductMap = saleDetailRepository
                .findBySaleProductIdIn(saleProducts.stream().map(SaleProduct::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(saleDetail -> saleDetail.getSaleProduct().getId()));

        saleProducts.forEach(saleProduct ->
                saleProduct.setSaleDetails(saleProductMap.getOrDefault(saleProduct.getId(), List.of())));

        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public SaleProductDto findById(Long id) {
        SaleProduct saleProduct = findSaleProduct(id);
        saleProduct.setSaleDetails(saleDetailRepository.findBySaleProductId(saleProduct.getId()));
        return mapper.toDto(saleProduct);
    }

    @Override
    @Transactional
    public SaleProductDto save(SaleProductDto dto) {
        validateSaleDetails(dto.saleDetails());

        SaleProduct saleProduct = mapper.toEntity(dto);
        saleProduct.setDiscount(resolveDiscount(dto.discount()));
        saleProduct.setPaidAmount(dto.paidAmount());
        saleProduct.setSoldAt(LocalDateTime.now());

        List<SaleDetail> saleDetails = dto.saleDetails()
                .stream()
                .map(this::resolveSaleDetail)
                .toList();

        BigDecimal totalAmount = calculateTotalAmount(saleDetails);
        BigDecimal paymentAmount = amountAfterDiscount(totalAmount, toPercentage(saleProduct.getDiscount()));

        saleProduct.setTotalAmount(totalAmount);
        saleProduct.setPaymentAmount(paymentAmount);

        decreaseStockOrThrow(saleDetails);

        SaleProduct savedSaleProduct = repository.save(saleProduct);

        for (SaleDetail saleDetail : saleDetails) {
            saleDetail.setSaleProduct(savedSaleProduct);
        }

        List<SaleDetail> savedSaleDetails = saleDetailRepository.saveAll(saleDetails);
        savedSaleProduct.setSaleDetails(savedSaleDetails);

        return mapper.toDto(savedSaleProduct);
    }

    @Override
    public SaleProductDto update(Long id, SaleProductDto dto) {
        throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED, UPDATE_NOT_ALLOWED_MESSAGE);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(findSaleProduct(id));
    }

    private SaleProduct findSaleProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SaleProduct.class, id));
    }

    private void validateSaleDetails(List<SaleDetailDto> saleDetails) {
        if (saleDetails == null || saleDetails.isEmpty()) {
            throw new ValidationException("Sale details must not be empty");
        }
    }

    private BigDecimal calculateTotalAmount(List<SaleDetail> saleDetails) {
        return saleDetails.stream()
                .map(this::calculateLineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateLineAmount(SaleDetail saleDetail) {
        return amountAfterDiscount(
                saleDetail.getSaleAmount().multiply(BigDecimal.valueOf(saleDetail.getQuantity())),
                toPercentage(saleDetail.getDiscount())
        );
    }

    private void decreaseStockOrThrow(List<SaleDetail> saleDetails) {
        for (SaleDetail saleDetail : saleDetails) {
            int updated = productRepository.decreaseStock(
                    saleDetail.getProduct().getId(),
                    saleDetail.getQuantity()
            );

            if (updated == 0) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "Product '%s' has insufficient stock.".formatted(saleDetail.getProduct().getName())
                );
            }
        }
    }

    private BigDecimal amountAfterDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        BigDecimal discountAmount = amount.multiply(discountPercentage)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return amount.subtract(discountAmount);
    }

    private BigDecimal toPercentage(Double discount) {
        return BigDecimal.valueOf(resolveDiscount(discount));
    }

    private double resolveDiscount(Double discount) {
        return discount == null ? 0.0 : discount;
    }

    private SaleDetail resolveSaleDetail(SaleDetailDto saleDetailDto) {
        SaleDetail saleDetail = saleDetailMapper.toEntity(saleDetailDto);
        saleDetail.setProduct(productRepository.findByIdForUpdate(saleDetailDto.productId())
                .orElseThrow(() -> new ResourceNotFoundException(Product.class, saleDetailDto.productId())));

        if (saleDetail.getProduct().getAvailableUnit() == 0) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "%s is currently out of stock.".formatted(saleDetail.getProduct().getName())
            );
        }

        if (saleDetail.getProduct().getAvailableUnit() < saleDetail.getQuantity()) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "%s has only %d units available, but you requested %d.".formatted(
                            saleDetail.getProduct().getName(),
                            saleDetail.getProduct().getAvailableUnit(),
                            saleDetail.getQuantity()
                    )
            );
        }

        return saleDetail;
    }
}
