package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.CategoryMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.repository.CategoryRepository;
import com.backend.phoneshop.repository.CategoryTypeRepository;
import com.backend.phoneshop.impl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryTypeRepository categoryTypeRepository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryServiceImpl service;

    private Category category;
    private CategoryDto dto;
    private Brand brand;
    private CategoryType categoryType;

    @BeforeEach
    void setUp() {
        brand = new Brand();
        brand.setId(1L);

        categoryType = new CategoryType();
        categoryType.setId(2L);

        category = new Category();
        category.setId(1L);
        category.setName("Phone");
        category.setBrand(brand);
        category.setCategoryType(categoryType);

        dto = CategoryDto.builder()
                .id(1L)
                .name("Phone")
                .brandId(1L)
                .categoryTypeId(2L)
                .build();
    }

    // ✅ findById
    @Test
    void shouldReturnCategoryById() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(dto);

        CategoryDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Phone", result.name());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

    // ✅ save
    @Test
    void shouldSaveCategory() {
        when(mapper.toEntity(dto)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.of(categoryType));
        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(dto);

        CategoryDto result = service.save(dto);

        assertNotNull(result);
        verify(repository, times(1)).save(category);
    }

    @Test
    void shouldThrowWhenBrandNotFound() {
        when(mapper.toEntity(dto)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));
    }

    // ✅ update
    @Test
    void shouldUpdateCategory() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.of(categoryType));
        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(dto);

        CategoryDto result = service.update(1L, dto);

        assertNotNull(result);
        verify(repository).save(category);
    }

    // ✅ delete
    @Test
    void shouldDeleteCategory() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        service.delete(1L);

        verify(repository).delete(category);
    }

    @Test
    void shouldThrowWhenDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));
    }

    // ✅ findAll (pagination)
    @Test
    void shouldReturnPagedCategories() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = new PageImpl<>(List.of(category));

        when(repository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(page);
        when(mapper.toDto(category)).thenReturn(dto);

        PageResponse<CategoryDto> result = service.findAll(new HashMap<>(), pageable);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }
}