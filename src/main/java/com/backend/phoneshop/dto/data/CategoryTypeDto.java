package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CategoryTypeDto(
    Long id,
    @NotBlank(message = "Name is required")
    String name
) {}
