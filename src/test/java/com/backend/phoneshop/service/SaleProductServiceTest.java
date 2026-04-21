package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.SaleDetailDto;
import com.backend.phoneshop.dto.SaleProductDto;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.SaleDetail;
import com.backend.phoneshop.entity.SaleProduct;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.exception.ValidationException;
import com.backend.phoneshop.impl.SaleProductServiceImpl;
import com.backend.phoneshop.mapper.SaleDetailMapper;
import com.backend.phoneshop.mapper.SaleProductMapper;
import com.backend.phoneshop.repository.ProductRepository;
import com.backend.phoneshop.repository.SaleDetailRepository;
import com.backend.phoneshop.repository.SaleProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleProductServiceTest {

    @Mock
    private SaleProductRepository repository;

    @Mock
    private SaleDetailRepository saleDetailRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SaleProductMapper mapper;

    @Mock
    private SaleDetailMapper saleDetailMapper;

    @InjectMocks
    private SaleProductServiceImpl service;

    @Test
    void save_shouldCalculateAmountsPersistSaleAndDecreaseStock() {
        SaleDetailDto firstDetailDto = saleDetailDto(1L, 2, "100.00", 10.0);
        SaleDetailDto secondDetailDto = saleDetailDto(2L, 1, "50.00", 0.0);
        SaleProductDto request = saleProductDto(List.of(firstDetailDto, secondDetailDto), "250.00", 5.0);

        SaleProduct mappedSaleProduct = SaleProduct.builder()
                .discount(5.0)
                .paidAmount(new BigDecimal("250.00"))
                .build();
        SaleProduct savedSaleProduct = SaleProduct.builder()
                .id(10L)
                .discount(5.0)
                .paidAmount(new BigDecimal("250.00"))
                .build();
        SaleDetail firstSaleDetail = saleDetail(2, "100.00", 10.0);
        SaleDetail secondSaleDetail = saleDetail(1, "50.00", 0.0);
        Product firstProduct = product(1L, "iPhone 15 Pro", 5);
        Product secondProduct = product(2L, "Galaxy S24", 10);
        SaleProductDto response = SaleProductDto.builder()
                .id(10L)
                .saleDetails(List.of(firstDetailDto, secondDetailDto))
                .totalAmount(new BigDecimal("230.00"))
                .paymentAmount(new BigDecimal("218.50"))
                .paidAmount(new BigDecimal("250.00"))
                .discount(5.0)
                .build();

        when(mapper.toEntity(request)).thenReturn(mappedSaleProduct);
        when(saleDetailMapper.toEntity(firstDetailDto)).thenReturn(firstSaleDetail);
        when(saleDetailMapper.toEntity(secondDetailDto)).thenReturn(secondSaleDetail);
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(secondProduct));
        when(productRepository.decreaseStock(1L, 2)).thenReturn(1);
        when(productRepository.decreaseStock(2L, 1)).thenReturn(1);
        when(repository.save(any(SaleProduct.class))).thenReturn(savedSaleProduct);
        when(saleDetailRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(savedSaleProduct)).thenReturn(response);

        SaleProductDto result = service.save(request);

        ArgumentCaptor<SaleProduct> saleProductCaptor = ArgumentCaptor.forClass(SaleProduct.class);
        verify(repository).save(saleProductCaptor.capture());

        SaleProduct persistedSaleProduct = saleProductCaptor.getValue();
        assertThat(persistedSaleProduct.getTotalAmount()).isEqualByComparingTo("230.00");
        assertThat(persistedSaleProduct.getPaymentAmount()).isEqualByComparingTo("218.50");
        assertThat(persistedSaleProduct.getPaidAmount()).isEqualByComparingTo("250.00");
        assertThat(persistedSaleProduct.getSoldAt()).isNotNull();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SaleDetail>> saleDetailsCaptor = ArgumentCaptor.forClass(List.class);
        verify(saleDetailRepository).saveAll(saleDetailsCaptor.capture());

        List<SaleDetail> persistedSaleDetails = saleDetailsCaptor.getValue();
        assertThat(persistedSaleDetails).hasSize(2);
        assertThat(persistedSaleDetails).allSatisfy(detail -> assertThat(detail.getSaleProduct()).isSameAs(savedSaleProduct));
        assertThat(firstSaleDetail.getProduct()).isSameAs(firstProduct);
        assertThat(secondSaleDetail.getProduct()).isSameAs(secondProduct);
        assertThat(savedSaleProduct.getSaleDetails()).hasSize(2);

        verify(productRepository).decreaseStock(1L, 2);
        verify(productRepository).decreaseStock(2L, 1);
        assertThat(result).isSameAs(response);
    }

    @Test
    void save_shouldThrowValidationException_whenPaidAmountIsLessThanPaymentAmount() {
        SaleDetailDto saleDetailDto = saleDetailDto(1L, 1, "100.00", 0.0);
        SaleProductDto request = saleProductDto(List.of(saleDetailDto), "90.00", 0.0);

        SaleProduct mappedSaleProduct = SaleProduct.builder()
                .discount(0.0)
                .paidAmount(new BigDecimal("90.00"))
                .build();
        SaleDetail saleDetail = saleDetail(1, "100.00", 0.0);
        Product product = product(1L, "iPhone 15 Pro", 5);

        when(mapper.toEntity(request)).thenReturn(mappedSaleProduct);
        when(saleDetailMapper.toEntity(saleDetailDto)).thenReturn(saleDetail);
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Paid amount must be greater than or equal to payment amount.");

        verify(productRepository, never()).decreaseStock(any(Long.class), any(Integer.class));
        verify(repository, never()).save(any(SaleProduct.class));
        verify(saleDetailRepository, never()).saveAll(any());
    }

    @Test
    void findById_shouldLoadSaleDetailsBeforeMapping() {
        SaleProduct saleProduct = SaleProduct.builder().id(7L).build();
        SaleDetail saleDetail = saleDetail(1, "100.00", 0.0);
        SaleProductDto response = SaleProductDto.builder()
                .id(7L)
                .discount(0.0)
                .build();

        when(repository.findById(7L)).thenReturn(Optional.of(saleProduct));
        when(saleDetailRepository.findBySaleProductId(7L)).thenReturn(List.of(saleDetail));
        when(mapper.toDto(any(SaleProduct.class))).thenReturn(response);

        SaleProductDto result = service.findById(7L);

        verify(mapper).toDto(argThat(foundSaleProduct ->
                foundSaleProduct.getId().equals(7L)
                        && foundSaleProduct.getSaleDetails().equals(List.of(saleDetail))
        ));
        assertThat(result).isSameAs(response);
    }

    @Test
    void update_shouldThrowMethodNotAllowedException() {
        SaleProductDto request = saleProductDto(List.of(saleDetailDto(1L, 1, "100.00", 0.0)), "100.00", 0.0);

        assertThatThrownBy(() -> service.cancel(1L))
                .isInstanceOf(ApiException.class)
                .satisfies(throwable -> {
                    ApiException exception = (ApiException) throwable;
                    assertThat(exception.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
                    assertThat(exception.getMessage()).isEqualTo("Updating sale products is not supported.");
                });

        verifyNoInteractions(repository, saleDetailRepository, productRepository, mapper, saleDetailMapper);
    }

    @Test
    void delete_shouldDeleteExistingSaleProduct() {
        SaleProduct saleProduct = SaleProduct.builder().id(9L).build();
        when(repository.findById(9L)).thenReturn(Optional.of(saleProduct));

        service.cancel(9L);

        verify(repository).delete(saleProduct);
    }

    private SaleProductDto saleProductDto(List<SaleDetailDto> saleDetails, String paidAmount, Double discount) {
        return SaleProductDto.builder()
                .saleDetails(saleDetails)
                .paidAmount(new BigDecimal(paidAmount))
                .discount(discount)
                .build();
    }

    private SaleDetailDto saleDetailDto(Long productId, int quantity, String saleAmount, Double discount) {
        return SaleDetailDto.builder()
                .productId(productId)
                .quantity(quantity)
                .saleAmount(new BigDecimal(saleAmount))
                .discount(discount)
                .build();
    }

    private SaleDetail saleDetail(int quantity, String saleAmount, Double discount) {
        return SaleDetail.builder()
                .quantity(quantity)
                .saleAmount(new BigDecimal(saleAmount))
                .discount(discount)
                .build();
    }

    private Product product(Long id, String name, int availableUnit) {
        return Product.builder()
                .id(id)
                .name(name)
                .availableUnit(availableUnit)
                .salePrice(new BigDecimal("999.99"))
                .build();
    }
}
