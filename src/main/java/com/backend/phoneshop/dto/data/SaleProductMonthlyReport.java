package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SaleProductMonthlyReport(
        String month,
        Long productId,
        String productName,
        String productUsedStatus,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        Long categoryTypeId,
        String categoryTypeName,
        Integer soleProductCount, // sum of quantity of sold product per month
        BigDecimal totalPaymentAmount, // sum of paymentAmount per month
        BigDecimal totalPaidAmount // sum of paidAmount per month
) {
}
