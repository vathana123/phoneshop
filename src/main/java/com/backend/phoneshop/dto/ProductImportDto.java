package com.backend.phoneshop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record ProductImportDto(
        Long id,
        @NotNull(message = "Import date is required")
        @PastOrPresent(message = "Import date cannot be in the future")
        Date importDate,
        @NotNull(message = "Import unit is required")
        @Min(value = 1, message = "Import unit must be at least 1")
        Integer importUnit,
        @DecimalMin(value = "0", message = "Unit price must be greater than 0")
        @NotNull(message = "Unit price is required")
        BigDecimal unitPrice,
        @NotBlank(message = "Product ID is required")
        Long productId
) {}
