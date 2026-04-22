package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryDto(
    Long id,
    @NotBlank(message = "Name is required")
    String name,
    @NotNull(message = "Brand ID is required")
    Long brandId,
    String brandName,
    @NotNull(message = "Category Type ID is required")
    Long categoryTypeId,
    String categoryTypeName
) {}
