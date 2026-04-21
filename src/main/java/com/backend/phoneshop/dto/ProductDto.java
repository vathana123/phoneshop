package com.backend.phoneshop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(
    Long id,
    @NotBlank(message = "Name is required")
    String name,
    String color,
    String spec,
    @NotBlank(message = "Use Status is required")
    String usedStatus,
    @DecimalMin(value = "0", message = "Used Quality must be at least 0.01")
    @DecimalMax(value = "100.00", message = "Used Quality must not exceed 100.00")
    Double usedQuality,
    String description,
    Integer availableUnit,
    Integer warrantyDuration,
    String imageUrl,
    @DecimalMin(value = "0", message = "Sale price not allow negative")
    @NotNull(message = "Sale price is required")
    BigDecimal salePrice,
    @NotNull(message = "Category ID is required")
    Long categoryId,
    String categoryName,
    String categoryType,
    String brand
) {}
