package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.entities.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.repository.CategoryTypeRepository;
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
public class CategoryTypeServiceTest {

    @Autowired
    private CategoryTypeService service;

    @Autowired
    private CategoryTypeRepository repository;

    @Test
    void shouldSaveCategoryType() {
        // Arrange
        CategoryTypeDto dto = CategoryTypeDto.builder().name("Smart Phone").build();

        // Act
        CategoryTypeDto result = service.save(dto);

        // Assert
        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Smart Phone");
    }

    @Test
    void shouldFindById() {
        // Arrange
        CategoryType brand = repository.save(CategoryType.builder().name("Charger").build());

        // Act
        CategoryTypeDto result = service.findById(brand.getId());

        // Assert
        assertThat(result.name()).isEqualTo("Charger");
    }

    @Test
    void shouldThrowException_whenNotFound() {
        // Act & Assert
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateCategoryType() {
        // Arrange
        CategoryType brand = repository.save(CategoryType.builder().name("Old").build());

        CategoryTypeDto updateDto = CategoryTypeDto.builder().name("New").build();

        // Act
        CategoryTypeDto updated = service.update(brand.getId(), updateDto);

        // Assert
        assertThat(updated.name()).isEqualTo("New");
    }

    @Test
    void shouldDeleteCategoryType() {
        // Arrange
        CategoryType brand = repository.save(CategoryType.builder().name("DeleteMe").build());

        // Act
        service.delete(brand.getId());

        // Assert
        assertThat(repository.findById(brand.getId())).isEmpty();
    }

    @Test
    void shouldReturnPagedResult() {
        // Arrange
        repository.save(CategoryType.builder().name("Smart Phone").build());
        repository.save(CategoryType.builder().name("Tablet").build());
        repository.save(CategoryType.builder().name("Cell Phone").build());

        Pageable pageable = PageRequest.of(0, 2);

        // Act
        PageResponse<CategoryTypeDto> response = service.findAll(null, pageable);

        // Assert
        assertThat(response.content()).hasSize(2);
        assertThat(response.totalElements()).isEqualTo(3);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.hasNext()).isTrue();
    }
}
