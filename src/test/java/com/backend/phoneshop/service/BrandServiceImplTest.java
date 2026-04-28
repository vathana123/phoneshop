package com.backend.phoneshop.service;
import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.impl.BrandServiceImpl;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.repository.BrandRepository;
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
class BrandServiceImplTest {
    @Mock
    private BrandRepository repository;

    @Mock
    private BrandMapper mapper;

    @InjectMocks
    private BrandServiceImpl service;

    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    void setUp() {
        brand = Brand.builder()
                .id(1L)
                .name("Apple")
                .build();

        brandDto = new BrandDto(1L, "Apple");
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        // Arrange
        Map<String, Object> filters = Map.of("search", "apple");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Brand> page = new PageImpl<>(List.of(brand));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(brand)).thenReturn(brandDto);

        // Act
        PageResponse<BrandDto> response = service.findAll(filters, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(brand);
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnBrandDto_whenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(brand));
        when(mapper.toDto(brand)).thenReturn(brandDto);

        // Act
        BrandDto result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Apple", result.name());

        verify(repository).findById(1L);
        verify(mapper).toDto(brand);
    }

    @Test
    void shouldThrowException_whenBrandNotFound() {
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
    void shouldSaveBrand_whenValidDto() {
        // Arrange
        when(mapper.toEntity(brandDto)).thenReturn(brand);
        when(repository.save(brand)).thenReturn(brand);
        when(mapper.toDto(brand)).thenReturn(brandDto);

        // Act
        BrandDto result = service.save(brandDto);

        // Assert
        assertNotNull(result);
        assertEquals("Apple", result.name());

        verify(mapper).toEntity(brandDto);
        verify(repository).save(brand);
        verify(mapper).toDto(brand);
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateBrand_whenIdExists() {
        // Arrange
        Brand updatedEntity = Brand.builder().id(1L).name("Apple Updated").build();
        BrandDto updatedDto = new BrandDto(1L, "Apple Updated");

        when(repository.findById(1L)).thenReturn(Optional.of(brand));
        when(mapper.mergeDto(brandDto, brand)).thenReturn(updatedEntity);
        when(repository.save(updatedEntity)).thenReturn(updatedEntity);
        when(mapper.toDto(updatedEntity)).thenReturn(updatedDto);

        // Act
        BrandDto result = service.update(1L, brandDto);

        // Assert
        assertNotNull(result);
        assertEquals("Apple Updated", result.name());

        verify(repository).findById(1L);
        verify(mapper).mergeDto(brandDto, brand);
        verify(repository).save(updatedEntity);
        verify(mapper).toDto(updatedEntity);
    }

    @Test
    void shouldThrowException_whenUpdateBrandNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, brandDto));

        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteBrand_whenIdExists() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(brand));

        // Act
        service.delete(1L);

        // Assert
        verify(repository).findById(1L);
        verify(repository).delete(brand);
    }

    @Test
    void shouldThrowException_whenDeleteBrandNotFound() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository).findById(1L);
        verify(repository, never()).delete(any(Brand.class));
    }
}
