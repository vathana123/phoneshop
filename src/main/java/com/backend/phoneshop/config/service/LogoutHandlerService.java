package com.backend.phoneshop.config.service;

import com.backend.phoneshop.entity.RefreshToken;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RefreshTokenRepository repository;
    private final JwtDecoder jwtDecoder;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // ✅ 1. Validate header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // ✅ 2. Decode JWT
            Jwt jwt = jwtDecoder.decode(token);

            // ✅ 3. Ensure this is a refresh token
            String scope = jwt.getClaimAsString("scope");
            if (!"REFRESH_TOKEN".equals(scope)) {
                log.warn("Logout attempted with non-refresh token");
                return;
            }

            // ✅ 4. Find token (only non-revoked due to @SQLRestriction)
            RefreshToken refreshToken = repository.findByToken(token)
                    .orElseThrow(() -> {
                        log.warn("Refresh token not found or already revoked");
                        return new ApiException(
                                HttpStatus.BAD_REQUEST,
                                "Refresh token not found"
                        );
                    });

            // ✅ 5. Soft delete → triggers @SQLDelete (sets revoked_at)
            repository.delete(refreshToken);

            log.info("Refresh token revoked successfully for user: {}",
                    refreshToken.getUser().getUsername());

        } catch (JwtException ex) {
            log.debug("Invalid JWT during logout: {}", ex.getMessage());
        }
    }
}
