package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.entities.Brand;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BrandServiceTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository repository;

    @Test
    void shouldSaveBrand() {
        // Arrange
        BrandDto dto = BrandDto.builder().name("Apple").build();

        // Act
        BrandDto result = brandService.save(dto);

        // Assert
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Apple");
    }

    @Test
    void shouldFindById() {
        // Arrange
        Brand brand = repository.save(Brand.builder().name("Samsung").build());

        // Act
        BrandDto result = brandService.findById(brand.getId());

        // Assert
        assertThat(result.name()).isEqualTo("Samsung");
    }

    @Test
    void shouldThrowException_whenNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> brandService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateBrand() {
        // Arrange
        Brand brand = repository.save(Brand.builder().name("Old").build());

        BrandDto updateDto = BrandDto.builder().name("New").build();

        // Act
        BrandDto updated = brandService.update(brand.getId(), updateDto);

        // Assert
        assertThat(updated.name()).isEqualTo("New");
    }

    @Test
    void shouldDeleteBrand() {
        // Arrange
        Brand brand = repository.save(Brand.builder().name("DeleteMe").build());

        // Act
        brandService.delete(brand.getId());

        // Assert
        assertThat(repository.findById(brand.getId())).isEmpty();
    }

    @Test
    void shouldReturnPagedResult() {
        // Arrange
        repository.save(Brand.builder().name("Apple").build());
        repository.save(Brand.builder().name("Samsung").build());
        repository.save(Brand.builder().name("Xiaomi").build());

        Pageable pageable = PageRequest.of(0, 2);

        // Act
        PageResponse<BrandDto> response = brandService.findAll(null, pageable);

        // Assert
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(3);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.hasNext()).isTrue();
    }
}
