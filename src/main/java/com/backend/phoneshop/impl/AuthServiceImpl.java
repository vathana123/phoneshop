package com.backend.phoneshop.impl;

import com.backend.phoneshop.config.service.JwtService;
import com.backend.phoneshop.dto.request.AuthRequest;
import com.backend.phoneshop.dto.respone.AuthResponse;
import com.backend.phoneshop.entity.RefreshToken;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.mapper.UserMapper;
import com.backend.phoneshop.repository.RefreshTokenRepository;
import com.backend.phoneshop.repository.UserRepository;
import com.backend.phoneshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;

    // ===============================
    // LOGIN
    // ===============================
    @Override
    public AuthResponse login(AuthRequest authRequest) {

        // 1. Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));

        // 2. Generate tokens
        String accessToken = jwtService.generateAccessToken(authentication, user);
        String refreshToken = jwtService.generateRefreshToken(authentication, user);

        // 3. Save refresh token in DB
        saveRefreshToken(user, refreshToken);

        // 4. Return response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry(3600) // seconds (1 hour)
                .tokenType("Bearer")
                .build();
    }

    // ===============================
    // REFRESH TOKEN
    // ===============================
    @Override
    public AuthResponse refreshToken(String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Authorization header");
        }

        String oldRefreshToken = authorizationHeader.substring(7);

        // 1. Decode JWT
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(oldRefreshToken);
        } catch (JwtException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        // 2. Validate token type
        String scope = jwt.getClaimAsString("scope");
        if (!"REFRESH_TOKEN".equals(scope)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
        }

        String username = jwt.getSubject();

        // 3. Load user
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        // 4. Check token exists in DB (and not revoked due to @SQLRestriction)
        RefreshToken storedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() ->
                        new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        // 5. Check expiration
        if (storedToken.getExpiresAt() != null &&
                storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {

            // revoke expired token
            refreshTokenRepository.delete(storedToken);

            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        // 🔥 6. ROTATE TOKEN (BEST PRACTICE)
        // revoke old token
        refreshTokenRepository.delete(storedToken);

        Set<GrantedAuthority> authorities =
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission ->
                                new SimpleGrantedAuthority(permission.getName()))
                        .collect(Collectors.toSet());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "",
                authorities
        );

        // create new authentication object
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );

        // 7. Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(authentication, user);
        String newRefreshToken = jwtService.generateRefreshToken(authentication, user);

        // 8. Save new refresh token
        saveRefreshToken(user, newRefreshToken);

        // 9. Return response
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiry(3600)
                .tokenType("Bearer")
                .build();
    }

    // ===============================
    // SAVE REFRESH TOKEN
    // ===============================
    private void saveRefreshToken(User user, String token) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
