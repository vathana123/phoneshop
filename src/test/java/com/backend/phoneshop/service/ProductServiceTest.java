package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.dto.ProductDto;
import com.backend.phoneshop.dto.ProductImportDto;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.ProductImport;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.impl.ProductServiceImpl;
import com.backend.phoneshop.mapper.ProductImportMapper;
import com.backend.phoneshop.mapper.ProductMapper;
import com.backend.phoneshop.repository.CategoryRepository;
import com.backend.phoneshop.repository.ProductImportRepository;
import com.backend.phoneshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductImportRepository productImportRepository;

    @Mock
    private ProductImportMapper productImportMapper;

    @InjectMocks
    private ProductServiceImpl service;

    private Category category;
    private Product product;
    private ProductDto dto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(10L);
        category.setName("Phone");

        product = new Product();
        product.setId(1L);
        product.setName("iPhone 15");
        product.setAvailableUnit(5);
        product.setSalePrice(new BigDecimal("999.99"));
        product.setCategory(category);

        dto = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .availableUnit(5)
                .salePrice(new BigDecimal("999.99"))
                .categoryId(10L)
                .build();
    }

    @Test
    void shouldReturnProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.toDto(product)).thenReturn(dto);

        ProductDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("iPhone 15", result.name());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void shouldSaveProduct() {
        when(mapper.toEntity(dto)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(dto);

        ProductDto result = service.save(dto);

        assertNotNull(result);
        verify(repository, times(1)).save(product);
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        when(mapper.toEntity(dto)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.save(dto));
    }

    @Test
    void shouldUpdateProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.mergeDto(dto, product)).thenReturn(product);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(dto);

        ProductDto result = service.update(1L, dto);

        assertNotNull(result);
        verify(repository).save(product);
    }

    @Test
    void shouldDeleteProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).delete(product);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void shouldReturnPagedProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(repository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(page);
        when(mapper.toDto(product)).thenReturn(dto);

        PageResponse<ProductDto> result = service.findAll(new HashMap<>(), pageable);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }

    @Test
    void shouldImportProductAndUpdateAvailableUnit() {
        // Arrange
        Long productId = 1L;

        ProductImportDto dto = ProductImportDto.builder()
                .id(1L)
                .importUnit(10)
                .unitPrice(new BigDecimal("500.00"))
                .productId(productId)
                .build();

        Product product = new Product();
        product.setId(productId);
        product.setAvailableUnit(5); // existing stock

        ProductImport entity = new ProductImport();
        ProductImport savedEntity = new ProductImport();

        ProductImportDto expectedDto = ProductImportDto.builder()
                .id(1L)
                .importUnit(10)
                .productId(productId)
                .build();

        when(repository.findById(productId)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(productImportMapper.toEntity(dto)).thenReturn(entity);
        when(productImportRepository.save(entity)).thenReturn(savedEntity);
        when(productImportMapper.toDto(savedEntity)).thenReturn(expectedDto);

        // Act
        ProductImportDto result = service.importProduct(dto);

        // Assert
        assertNotNull(result);
        assertEquals(15, product.getAvailableUnit()); // ✅ 5 + 10 = 15

        verify(repository).save(product); // ensure product updated
        verify(productImportRepository).save(entity); // ensure import saved
    }
}
