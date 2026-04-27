package com.backend.phoneshop.dto.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleDto(
        Long id,
        @NotNull(message = "Role Name is required")
        String name,
        @NotEmpty(message = "Permission list is required")
        Set<PermissionDto> permissions
) {}
