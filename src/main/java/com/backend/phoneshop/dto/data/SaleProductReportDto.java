package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SaleProductReportDto(
        String productName,
        String productUsedStatus,
        String categoryName,
        String brandName,
        String categoryTypeName,
        LocalDateTime soldAt,
        Integer quantity,
        BigDecimal saleAmount,
        Double discount
) {}
