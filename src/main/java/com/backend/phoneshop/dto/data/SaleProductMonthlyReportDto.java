package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SaleProductMonthlyReportDto(
        String month,
        String productName,
        String productUsedStatus,
        String categoryName,
        String brandName,
        String categoryTypeName,
        BigDecimal soleProductCount, // sum of quantity of sold product per month
        BigDecimal totalPaymentAmount, // sum of paymentAmount per month
        BigDecimal totalPaidAmount // sum of paidAmount per month
) {}