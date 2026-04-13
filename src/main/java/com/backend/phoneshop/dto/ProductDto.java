package com.backend.phoneshop.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(
    Long id,
    String name,
    String color,
    String spec,
    String usedStatus,
    Double usedQuality,
    String description,
    Integer availableUnit,
    Integer warrantyDuration,
    String imageUrl,
    BigDecimal salePrice,
    Long categoryId,
    String categoryName,
    String categoryType,
    String brand
) {}
