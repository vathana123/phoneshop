package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SaleDetailDto(
        Long id,
        @NotNull(message = "Product ID is required")
        Long productId,
        String productName,
        @Min(value = 1, message = "Quantity must be greater than 0")
        @NotNull(message = "Quantity is required")
        Integer quantity,
        Long saleProductId,
        @DecimalMin(value = "0", message = "Sale amount must not be negative")
        @NotNull(message = "Sale amount is required")
        BigDecimal saleAmount,
        @DecimalMin(value = "0.00", message = "Discount must be at least 0")
        @DecimalMax(value = "100.00", message = "Discount must not exceed 100.00")
        Double discount
) {
    public SaleDetailDto {
        if (discount == null) {
            discount = 0.00;
        }
    }
}
