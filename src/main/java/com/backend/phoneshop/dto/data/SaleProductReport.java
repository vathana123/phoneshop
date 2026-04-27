package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
public record SaleProductReport(
        Long saleProductId,
        Long saleDetailId,
        Long productId,
        String productName,
        String productUsedStatus,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        Long categoryTypeId,
        String categoryTypeName,
        Timestamp soldAt,
        Integer quantity,
        BigDecimal saleAmount,
        Double discount
) {}
