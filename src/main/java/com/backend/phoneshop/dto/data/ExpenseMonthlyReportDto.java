package com.backend.phoneshop.dto.data;

import java.math.BigDecimal;

public record ExpenseMonthlyReportDto(
        String month,
        String productName,
        String productUsedStatus,
        String categoryName,
        String brandName,
        String categoryTypeName,
        BigDecimal importProductCount,
        BigDecimal totalExpenseAmount,
        Long importCount
) {}
