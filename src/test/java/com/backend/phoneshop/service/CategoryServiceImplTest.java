package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.CategoryDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.impl.CategoryServiceImpl;
import com.backend.phoneshop.mapper.CategoryMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.repository.CategoryRepository;
import com.backend.phoneshop.repository.CategoryTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

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
        brand = Brand.builder().id(1L).name("Apple").build();
        categoryType = CategoryType.builder().id(2L).name("Phone").build();

        category = Category.builder()
                .id(1L)
                .name("iPhone")
                .brand(brand)
                .categoryType(categoryType)
                .build();

        dto = new CategoryDto(
                1L,
                "iPhone",
                1L,
                "Apple",
                2L,
                "Phone"
        );
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        // Arrange
        Map<String, Object> filters = Map.of("search", "iphone");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> page = new PageImpl<>(List.of(category));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(category)).thenReturn(dto);

        // Act
        PageResponse<CategoryDto> response = service.findAll(filters, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(category);
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnCategory_whenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.toDto(category)).thenReturn(dto);

        // Act
        CategoryDto result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("iPhone", result.name());

        verify(repository).findById(1L);
        verify(mapper).toDto(category);
    }

    @Test
    void shouldThrowException_whenCategoryNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));

        verify(repository).findById(1L);
        verify(mapper, never()).toDto(any());
    }

// ===============================
// SAVE
// ===============================

    @Test
    void shouldSaveCategory_whenValidDto() {
        // Arrange
        when(mapper.toEntity(dto)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.of(categoryType));
        when(repository.save(category)).thenReturn(category);
        when(mapper.toDto(category)).thenReturn(dto);

        // Act
        CategoryDto result = service.save(dto);

        // Assert
        assertNotNull(result);
        assertEquals("iPhone", result.name());

        verify(mapper).toEntity(dto);
        verify(brandRepository).findById(1L);
        verify(categoryTypeRepository).findById(2L);
        verify(repository).save(category);
        verify(mapper).toDto(category);
    }

    @Test
    void shouldThrowException_whenBrandNotFound_onSave() {
        // Arrange
        when(mapper.toEntity(dto)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenCategoryTypeNotFound_onSave() {
        // Arrange
        when(mapper.toEntity(dto)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));

        verify(repository, never()).save(any());
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateCategory_whenValid() {
        // Arrange
        Category merged = Category.builder().id(1L).name("Updated").build();
        CategoryDto updatedDto = new CategoryDto(1L, "Updated", 1L, "Apple", 2L, "Phone");

        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.mergeDto(dto, category)).thenReturn(merged);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.of(categoryType));
        when(repository.save(merged)).thenReturn(merged);
        when(mapper.toDto(merged)).thenReturn(updatedDto);

        // Act
        CategoryDto result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.name());

        verify(repository).findById(1L);
        verify(mapper).mergeDto(dto, category);
        verify(repository).save(merged);
        verify(mapper).toDto(merged);
    }

    @Test
    void shouldThrowException_whenCategoryNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

    @Test
    void shouldThrowException_whenBrandNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.mergeDto(dto, category)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

    @Test
    void shouldThrowException_whenCategoryTypeNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));
        when(mapper.mergeDto(dto, category)).thenReturn(category);
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(categoryTypeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteCategory_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(category));

        service.delete(1L);

        verify(repository).findById(1L);
        verify(repository).delete(category);
    }

    @Test
    void shouldThrowException_whenDeleteCategoryNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, never()).delete(any(Category.class));
    }
}
