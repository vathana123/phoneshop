package com.backend.phoneshop.dto.request;

import lombok.Builder;

@Builder
public record AuthRequest(
        String username,
        String password
) {
}
