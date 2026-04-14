package com.backend.phoneshop.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record ProductImportDto(
        Long id,
        Date importDate,
        Integer importUnit,
        BigDecimal unitPrice,
        Long productId
) {
}
