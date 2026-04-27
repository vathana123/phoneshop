package com.backend.phoneshop.dto.data;

import java.math.BigDecimal;

public record ExpenseMonthlyReport(
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
        BigDecimal importProductCount,
        BigDecimal totalExpenseAmount,
        Long importCount
) {}
