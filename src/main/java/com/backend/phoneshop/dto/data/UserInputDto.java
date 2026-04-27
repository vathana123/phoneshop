package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserInputDto(
        @NotNull(message = "Name is required")
        String name,
        @NotNull(message = "Username is required")
        String username,
        @NotEmpty(message = "Role list is required")
        Set<Long> roles
) {}
