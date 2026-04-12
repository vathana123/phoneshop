package com.backend.phoneshop.dto;

import lombok.Builder;

@Builder
public record BrandDto (
    Long id,
    String name
){}
