package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.impl.CategoryTypeServiceImpl;
import com.backend.phoneshop.mapper.CategoryTypeMapper;
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
class CategoryTypeServiceImplTest {

    @Mock
    private CategoryTypeRepository repository;

    @Mock
    private CategoryTypeMapper mapper;

    @InjectMocks
    private CategoryTypeServiceImpl service;

    private CategoryType entity;
    private CategoryTypeDto dto;

    @BeforeEach
    void setUp() {
        entity = CategoryType.builder()
                .id(1L)
                .name("Phone")
                .build();

        dto = new CategoryTypeDto(1L, "Phone");
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        // Arrange
        Map<String, Object> filters = Map.of("search", "phone");
        Pageable pageable = PageRequest.of(0, 10);

        Page<CategoryType> page = new PageImpl<>(List.of(entity));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        PageResponse<CategoryTypeDto> response = service.findAll(filters, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("Phone", response.content().get(0).name());

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(entity);
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnCategoryType_whenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        CategoryTypeDto result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Phone", result.name());

        verify(repository).findById(1L);
        verify(mapper).toDto(entity);
    }

    @Test
    void shouldThrowException_whenCategoryTypeNotFound() {
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
    void shouldSaveCategoryType_whenValidDto() {
        // Arrange
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        CategoryTypeDto result = service.save(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Phone", result.name());

        verify(mapper).toEntity(dto);
        verify(repository).save(entity);
        verify(mapper).toDto(entity);
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateCategoryType_whenIdExists() {
        // Arrange
        CategoryType merged = CategoryType.builder()
                .id(1L)
                .name("Updated")
                .build();

        CategoryTypeDto updatedDto = new CategoryTypeDto(1L, "Updated");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.mergeDto(dto, entity)).thenReturn(merged);
        when(repository.save(merged)).thenReturn(merged);
        when(mapper.toDto(merged)).thenReturn(updatedDto);

        // Act
        CategoryTypeDto result = service.update(1L, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.name());

        verify(repository).findById(1L);
        verify(mapper).mergeDto(dto, entity);
        verify(repository).save(merged);
        verify(mapper).toDto(merged);
    }

    @Test
    void shouldThrowException_whenUpdateCategoryTypeNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));

        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteCategoryType_whenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // Act
        service.delete(1L);

        // Assert
        verify(repository).findById(1L);
        verify(repository).delete(entity);
    }

    @Test
    void shouldThrowException_whenDeleteCategoryTypeNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository).findById(1L);
        verify(repository, never()).delete(any(CategoryType.class));
    }
}
