package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
public record SaleProductReportDto(
        String productName,
        String productUsedStatus,
        String categoryName,
        String brandName,
        String categoryTypeName,
        Timestamp soldAt,
        Integer quantity,
        BigDecimal saleAmount,
        Double discount
) {}
