package com.backend.phoneshop.config.service;

import com.backend.phoneshop.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private static final String ISSUER = "https://phoneshop.com";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final String CLAIM_SCOPE = "scope";
    private static final String CLAIM_VERSION = "version";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    // ===============================
    public String getUserName(Jwt jwtToken){
        return jwtToken.getSubject();
    }

    // ===============================
    public boolean isTokenValid(Jwt jwtToken, User user){

        String username = jwtToken.getSubject();

        boolean isExpired = Objects.requireNonNull(jwtToken.getExpiresAt())
                .isBefore(Instant.now());

        boolean isSameUser = username.equals(user.getUsername());

        boolean isIssuerValid =
                ISSUER.equals(jwtToken.getIssuer().toString());

        // 🔥 version check (critical)
        Long tokenVersion = jwtToken.getClaim(CLAIM_VERSION);

        boolean isVersionValid =
                tokenVersion != null &&
                        tokenVersion.equals(Long.valueOf(user.getTokenVersion()));

        log.info("User Token Version: {}", user.getTokenVersion());
        log.info("Jwt Token Version: {}", tokenVersion);
        log.info("Version is valid: {}", isVersionValid);

        // 🔥 prevent refresh token misuse
        String scope = jwtToken.getClaimAsString(CLAIM_SCOPE);
        boolean isAccessToken =
                !"REFRESH_TOKEN".equals(scope);

        return !isExpired
                && isSameUser
                && isIssuerValid
                && isVersionValid
                && isAccessToken;
    }

    // ===============================
    public String generateAccessToken(Authentication authentication, User user) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(userDetails.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .claim(CLAIM_PERMISSIONS,
                        userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
                .claim(CLAIM_VERSION, user.getTokenVersion())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    // ===============================
    public String generateRefreshToken(Authentication authentication, User user) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(userDetails.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .claim(CLAIM_SCOPE, "REFRESH_TOKEN")
                .claim(CLAIM_VERSION, user.getTokenVersion())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    // ===============================
    public Authentication getAuthentication(String token) {

        Jwt jwt = jwtDecoder.decode(token);

        String username = jwt.getSubject();

        List<GrantedAuthority> authorities =
                Optional.ofNullable(jwt.getClaimAsStringList(CLAIM_PERMISSIONS))
                        .orElse(List.of())
                        .stream()
                        .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                        .toList();

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }
}