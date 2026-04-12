package com.backend.phoneshop.dto;

import lombok.Builder;

@Builder
public record CategoryDto(
    Long id,
    String name,
    Long brandId,
    String brandName,
    Long categoryTypeId,
    String categoryTypeName
) {}
