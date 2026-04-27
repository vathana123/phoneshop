package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.NotNull;

public record PermissionDto(
        @NotNull(message = "Permission ID is required")
        Long id,
        String name,
        String recourseTarget
) {
}
