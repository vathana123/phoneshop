package com.backend.phoneshop.dto.data;

import java.math.BigDecimal;

public record ExpenseMonthlyReportDto(
        String month,
        String productName,
        String productUsedStatus,
        String categoryName,
        String brandName,
        String categoryTypeName,
        Integer importProductCount,
        BigDecimal totalExpenseAmount,
        Integer importCount
) {}
