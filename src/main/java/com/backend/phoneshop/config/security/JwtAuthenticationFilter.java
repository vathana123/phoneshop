package com.backend.phoneshop.config.security;

import com.backend.phoneshop.config.service.JwtService;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_REQUIRED_MESSAGE =
            "Authentication token is required.";
    private static final String INVALID_TOKEN_MESSAGE =
            "Invalid token.";
    private static final String EXPIRED_TOKEN_MESSAGE =
            "Token has expired. Please log in again.";
    private static final String INACTIVE_TOKEN_MESSAGE =
            "Token is not active yet.";

    private final JwtDecoder jwtDecoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return HttpMethod.OPTIONS.matches(request.getMethod())
                || "/auth/login".equals(path)
                || "/auth/refresh".equals(path);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);

            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getSubject();

            if (username == null || username.isBlank()) {
                resolveUnauthorized(request, response, INVALID_TOKEN_MESSAGE);
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));

                if (!jwtService.isTokenValid(jwt, user)) {
                    log.debug("JWT failed validation for user: {}", username);
                    SecurityContextHolder.clearContext();
                    resolveUnauthorized(request, response, INVALID_TOKEN_MESSAGE);
                    return;
                }

                log.info("JWT is valid for user: {}", username);

                Authentication authentication = jwtService.getAuthentication(token);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                authentication.getPrincipal(),
                                null,
                                authentication.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (JwtException ex) {
            String message = resolveJwtMessage(ex);
            log.debug("Rejected JWT on {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage());
            SecurityContextHolder.clearContext();
            resolveUnauthorized(request, response, message);
            return;
        } catch (ApiException ex) {
            log.debug("Rejected token on {} {}: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getMessage());
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }

        filterChain.doFilter(request, response);
    }

    public static String missingTokenMessage() {
        return TOKEN_REQUIRED_MESSAGE;
    }

    private String resolveJwtMessage(JwtException ex) {
        if (ex instanceof JwtValidationException validationException) {
            for (OAuth2Error error : validationException.getErrors()) {
                String description = error.getDescription();

                if (description == null) {
                    continue;
                }

                if (description.contains("Jwt expired at")) {
                    return EXPIRED_TOKEN_MESSAGE;
                }

                if (description.contains("Jwt used before")) {
                    return INACTIVE_TOKEN_MESSAGE;
                }
            }
        }

        return INVALID_TOKEN_MESSAGE;
    }

    private void resolveUnauthorized(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String message) {
        handlerExceptionResolver.resolveException(
                request,
                response,
                null,
                new ApiException(HttpStatus.UNAUTHORIZED, message)
        );
    }
}
