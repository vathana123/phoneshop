package com.backend.phoneshop.dto.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record SaleProductDto(
        Long id,
        @NotEmpty(message = "Sale details must not be empty")
        List<@Valid SaleDetailDto> saleDetails,
        BigDecimal totalAmount,
        BigDecimal paymentAmount,
        @DecimalMin(value = "0", message = "Paid amount must not be negative")
        @NotNull(message = "Paid amount is required")
        BigDecimal paidAmount,
        @DecimalMin(value = "0.00", message = "Discount must be at least 0")
        @DecimalMax(value = "100.00", message = "Discount must not exceed 100.00")
        Double discount
) {
    public SaleProductDto {
        if (discount == null) {
            discount = 0.00;
        }
    }
}
