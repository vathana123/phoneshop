package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.SaleDetailDto;
import com.backend.phoneshop.dto.data.SaleProductDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.SaleDetail;
import com.backend.phoneshop.entity.SaleProduct;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.exception.ValidationException;
import com.backend.phoneshop.impl.SaleProductServiceImpl;
import com.backend.phoneshop.mapper.SaleDetailMapper;
import com.backend.phoneshop.mapper.SaleProductMapper;
import com.backend.phoneshop.repository.ProductRepository;
import com.backend.phoneshop.repository.SaleDetailRepository;
import com.backend.phoneshop.repository.SaleProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleProductServiceImplTest {
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

    private SaleProduct saleProduct;
    private SaleProductDto dto;
    private SaleDetailDto detailDto;
    private SaleDetail saleDetail;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("iPhone")
                .availableUnit(10)
                .build();

        detailDto = new SaleDetailDto(
                1L,
                1L,
                "iPhone",
                2,
                1L,
                BigDecimal.valueOf(1000),
                0.0
        );

        dto = new SaleProductDto(
                1L,
                List.of(detailDto),
                null,
                null,
                BigDecimal.valueOf(2000),
                0.0
        );

        saleDetail = SaleDetail.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .saleAmount(BigDecimal.valueOf(1000))
                .discount(0.0)
                .build();

        saleProduct = SaleProduct.builder()
                .id(1L)
                .discount(0.0)
                .build();
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SaleProduct> page = new PageImpl<>(List.of(saleProduct));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(saleDetailRepository.findBySaleProductIdIn(any()))
                .thenReturn(List.of(saleDetail));
        when(mapper.toDto(saleProduct)).thenReturn(dto);

        PageResponse<SaleProductDto> response = service.findAll(Map.of(), pageable);

        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(saleDetailRepository).findBySaleProductIdIn(any());
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnSaleProduct_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(saleProduct));
        when(saleDetailRepository.findBySaleProductId(1L))
                .thenReturn(List.of(saleDetail));
        when(mapper.toDto(saleProduct)).thenReturn(dto);

        SaleProductDto result = service.findById(1L);

        assertNotNull(result);
        verify(saleDetailRepository).findBySaleProductId(1L);
    }

    @Test
    void shouldThrowException_whenSaleProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

// ===============================
// SAVE
// ===============================

    @Test
    void shouldSaveSaleProduct_whenValid() {
        when(mapper.toEntity(dto)).thenReturn(saleProduct);
        when(saleDetailMapper.toEntity(detailDto)).thenReturn(saleDetail);
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(1L, 2)).thenReturn(1);

        when(repository.save(any())).thenReturn(saleProduct);
        when(saleDetailRepository.saveAll(any())).thenReturn(List.of(saleDetail));
        when(mapper.toDto(any())).thenReturn(dto);

        SaleProductDto result = service.save(dto);

        assertNotNull(result);

        verify(productRepository).decreaseStock(1L, 2);
        verify(repository).save(any());
        verify(saleDetailRepository).saveAll(any());
    }

    @Test
    void shouldThrowException_whenSaleDetailsEmpty() {
        SaleProductDto invalidDto = new SaleProductDto(
                1L,
                List.of(),
                null,
                null,
                BigDecimal.ONE,
                0.0
        );

        assertThrows(ValidationException.class,
                () -> service.save(invalidDto));
    }

    @Test
    void shouldThrowException_whenProductNotFound() {
        when(mapper.toEntity(dto)).thenReturn(saleProduct);
        when(saleDetailMapper.toEntity(detailDto)).thenReturn(saleDetail);
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));
    }

    @Test
    void shouldThrowException_whenStockInsufficient() {
        when(mapper.toEntity(dto)).thenReturn(saleProduct);
        when(saleDetailMapper.toEntity(detailDto)).thenReturn(saleDetail);
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(productRepository.decreaseStock(1L, 2)).thenReturn(0);

        assertThrows(ApiException.class,
                () -> service.save(dto));
    }

// ===============================
// CANCEL
// ===============================

    @Test
    void shouldCancelSaleProduct_whenValid() {
        when(repository.findById(1L)).thenReturn(Optional.of(saleProduct));
        when(saleDetailRepository.findBySaleProductId(1L))
                .thenReturn(List.of(saleDetail));
        when(productRepository.increaseStock(1L, 2)).thenReturn(1);

        service.cancel(1L);

        verify(productRepository).increaseStock(1L, 2);
        verify(repository).delete(saleProduct);
    }

    @Test
    void shouldThrowException_whenCancelStockFails() {
        when(repository.findById(1L)).thenReturn(Optional.of(saleProduct));
        when(saleDetailRepository.findBySaleProductId(1L))
                .thenReturn(List.of(saleDetail));
        when(productRepository.increaseStock(1L, 2)).thenReturn(0);

        assertThrows(ApiException.class,
                () -> service.cancel(1L));
    }
}
