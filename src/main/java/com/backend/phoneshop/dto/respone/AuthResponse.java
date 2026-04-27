package com.backend.phoneshop.dto.respone;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        Integer accessTokenExpiry,
        String tokenType
) {
}
