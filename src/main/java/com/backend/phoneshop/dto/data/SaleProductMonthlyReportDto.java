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
        Integer importProductCount,
        BigDecimal totalExpenseAmount,
        Integer importCount // Count time that we import this product in month(s)
) {}