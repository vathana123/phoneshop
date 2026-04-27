package com.backend.phoneshop.dto.data;

import lombok.Builder;

@Builder
public record UserRoleDto(
        Long id,
        String name
) {}
