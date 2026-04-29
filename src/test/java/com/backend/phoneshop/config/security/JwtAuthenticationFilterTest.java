package com.backend.phoneshop.config.security;

import com.backend.phoneshop.config.service.JwtService;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.repository.UserRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtDecoder,
                jwtService,
                userRepository,
                handlerExceptionResolver
        );
    }

    @Test
    void shouldResolveUnauthorized_whenJwtSignatureIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/brands");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bad-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtDecoder.decode("bad-token"))
                .thenThrow(new JwtException("Signed JWT rejected: Invalid signature"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                argThat(ex -> ex instanceof ApiException apiException
                        && apiException.getStatus() == HttpStatus.UNAUTHORIZED
                        && "Invalid token.".equals(apiException.getMessage()))
        );
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldResolveUnauthorized_whenJwtIsExpired() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/brands");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer expired-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        JwtValidationException ex = new JwtValidationException(
                "Jwt expired at 2026-04-29T00:00:00Z",
                List.of(new OAuth2Error("invalid_token", "Jwt expired at 2026-04-29T00:00:00Z", null))
        );

        when(jwtDecoder.decode("expired-token")).thenThrow(ex);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                argThat(error -> error instanceof ApiException apiException
                        && apiException.getStatus() == HttpStatus.UNAUTHORIZED
                        && "Token has expired. Please log in again.".equals(apiException.getMessage()))
        );
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldResolveUnauthorized_whenTokenFailsBusinessValidation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/brands");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        Jwt jwt = mock(Jwt.class);
        User user = mock(User.class);

        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(jwt, user)).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(
                eq(request),
                eq(response),
                isNull(),
                argThat(ex -> ex instanceof ApiException apiException
                        && apiException.getStatus() == HttpStatus.UNAUTHORIZED
                        && "Invalid token.".equals(apiException.getMessage()))
        );
        verify(filterChain, never()).doFilter(any(), any());
    }
}
