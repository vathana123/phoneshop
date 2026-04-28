package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.data.ProductImportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImportRepository productImportRepository;

    @Mock
    private ProductMapper mapper;

    @Mock
    private ProductImportMapper productImportMapper;

    @InjectMocks
    private ProductServiceImpl service;

    private Product product;
    private ProductDto dto;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Phone").build();

        product = Product.builder()
                .id(1L)
                .name("iPhone")
                .availableUnit(10)
                .category(category)
                .build();

        dto = new ProductDto(
                1L,
                "iPhone",
                null,
                null,
                "NEW",
                null,
                null,
                10,
                null,
                null,
                BigDecimal.valueOf(1000),
                1L,
                "Phone",
                null,
                null
        );
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        Map<String, Object> filters = Map.of("search", "iphone");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page = new PageImpl<>(List.of(product));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(product)).thenReturn(dto);

        PageResponse<ProductDto> response = service.findAll(filters, pageable);

        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(product);
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnProduct_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.toDto(product)).thenReturn(dto);

        ProductDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("iPhone", result.name());
    }

    @Test
    void shouldThrowException_whenProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

// ===============================
// SAVE
// ===============================

    @Test
    void shouldSaveProduct_whenValid() {
        when(mapper.toEntity(dto)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(product)).thenReturn(product);
        when(mapper.toDto(product)).thenReturn(dto);

        ProductDto result = service.save(dto);

        assertNotNull(result);

        verify(mapper).toEntity(dto);
        verify(categoryRepository).findById(1L);
        verify(repository).save(product);
        verify(mapper).toDto(product);
    }

    @Test
    void shouldThrowException_whenCategoryNotFound_onSave() {
        when(mapper.toEntity(dto)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));

        verify(repository, never()).save(any());
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateProduct_whenValid() {
        Product merged = Product.builder().id(1L).name("Updated").build();
        ProductDto updatedDto = new ProductDto(
                1L, "Updated", null, null, "NEW", null,
                null, 10, null, null,
                BigDecimal.valueOf(1000),
                1L, "Phone", null, null
        );

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.mergeDto(dto, product)).thenReturn(merged);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(repository.save(merged)).thenReturn(merged);
        when(mapper.toDto(merged)).thenReturn(updatedDto);

        ProductDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("Updated", result.name());
    }

    @Test
    void shouldThrowException_whenProductNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

    @Test
    void shouldThrowException_whenCategoryNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.mergeDto(dto, product)).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteProduct_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).delete(product);
    }

    @Test
    void shouldThrowException_whenDeleteProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, never()).delete(any(Product.class));
    }

// ===============================
// IMPORT PRODUCT
// ===============================

    @Test
    void shouldImportProduct_whenValid() {
        ProductImportDto importDto = mock(ProductImportDto.class);
        ProductImport entity = mock(ProductImport.class);
        ProductImportDto resultDto = mock(ProductImportDto.class);

        when(importDto.productId()).thenReturn(1L);
        when(importDto.importUnit()).thenReturn(5);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);

        when(productImportMapper.toEntity(importDto)).thenReturn(entity);
        when(productImportRepository.save(entity)).thenReturn(entity);
        when(productImportMapper.toDto(entity)).thenReturn(resultDto);

        ProductImportDto result = service.importProduct(importDto);

        assertNotNull(result);

        verify(repository).save(product);
        verify(productImportRepository).save(entity);
    }

    @Test
    void shouldThrowException_whenProductNotFound_onImport() {
        ProductImportDto importDto = mock(ProductImportDto.class);

        when(importDto.productId()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.importProduct(importDto));
    }
}
