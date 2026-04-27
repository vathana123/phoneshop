package com.backend.phoneshop.dto.data;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserDto(
        Long id,
        String name,
        String username,
        Set<UserRoleDto> roles
) {}
