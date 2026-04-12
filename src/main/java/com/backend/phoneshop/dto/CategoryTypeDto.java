package com.backend.phoneshop.dto;

import lombok.Builder;

@Builder
public record CategoryTypeDto(
    Long id,
    String name
) {}
