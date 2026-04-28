package com.backend.phoneshop.service;

import com.backend.phoneshop.config.service.JwtService;
import com.backend.phoneshop.dto.request.AuthRequest;
import com.backend.phoneshop.dto.respone.AuthResponse;
import com.backend.phoneshop.entity.Permission;
import com.backend.phoneshop.entity.RefreshToken;
import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.impl.AuthServiceImpl;
import com.backend.phoneshop.repository.RefreshTokenRepository;
import com.backend.phoneshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        authentication = mock(Authentication.class);
    }

// ===============================
// LOGIN
// ===============================

    @Test
    void shouldReturnTokens_whenLoginSuccess() {
        // Arrange
        AuthRequest request = new AuthRequest("test", "123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(authentication, user))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(authentication, user))
                .thenReturn("refresh-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername("test");
        verify(jwtService).generateAccessToken(authentication, user);
        verify(jwtService).generateRefreshToken(authentication, user);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void shouldThrowException_whenUsernameNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest("test", "123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> authService.login(request));

        verify(userRepository).findByUsername("test");
        verify(refreshTokenRepository, never()).save(any());
    }

// ===============================
// REFRESH TOKEN
// ===============================

    @Test
    void shouldThrowException_whenAuthorizationHeaderInvalid() {
        // Act & Assert
        assertThrows(ApiException.class,
                () -> authService.refreshToken(null));

        assertThrows(ApiException.class,
                () -> authService.refreshToken("InvalidToken"));
    }

    @Test
    void shouldThrowException_whenScopeIsNotRefreshToken() {
        // Arrange
        String token = "Bearer abc";

        Jwt jwt = mock(Jwt.class);

        when(jwtDecoder.decode("abc")).thenReturn(jwt);
        when(jwt.getClaimAsString("scope")).thenReturn("ACCESS_TOKEN");

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.refreshToken(token));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void shouldThrowException_whenRefreshTokenNotFoundInDb() {
        // Arrange
        String token = "Bearer abc";

        Jwt jwt = mock(Jwt.class);

        when(jwtDecoder.decode("abc")).thenReturn(jwt);
        when(jwt.getClaimAsString("scope")).thenReturn("REFRESH_TOKEN");
        when(jwt.getSubject()).thenReturn("user");

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(refreshTokenRepository.findByToken("abc"))
                .thenReturn(Optional.empty());

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.refreshToken(token));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void shouldThrowException_whenRefreshTokenExpired() {
        // Arrange
        String token = "Bearer abc";

        Jwt jwt = mock(Jwt.class);
        RefreshToken storedToken = mock(RefreshToken.class);

        when(jwtDecoder.decode("abc")).thenReturn(jwt);
        when(jwt.getClaimAsString("scope")).thenReturn("REFRESH_TOKEN");
        when(jwt.getSubject()).thenReturn("user");

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(refreshTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(storedToken));

        when(storedToken.getExpiresAt())
                .thenReturn(LocalDateTime.now().minusDays(1));

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> authService.refreshToken(token));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());

        verify(refreshTokenRepository).delete(storedToken);
    }

    @Test
    void shouldReturnNewTokens_whenRefreshTokenValid() {
        // Arrange
        String token = "Bearer abc";

        Jwt jwt = mock(Jwt.class);
        RefreshToken storedToken = mock(RefreshToken.class);

        Role role = mock(Role.class);
        Permission permission = mock(Permission.class);

        when(permission.getName()).thenReturn("READ");
        when(role.getPermissions()).thenReturn(Set.of(permission));
        when(user.getRoles()).thenReturn(Set.of(role));

        when(jwtDecoder.decode("abc")).thenReturn(jwt);
        when(jwt.getClaimAsString("scope")).thenReturn("REFRESH_TOKEN");
        when(jwt.getSubject()).thenReturn("user");

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(refreshTokenRepository.findByToken("abc"))
                .thenReturn(Optional.of(storedToken));

        when(storedToken.getExpiresAt())
                .thenReturn(LocalDateTime.now().plusDays(1));

        when(jwtService.generateAccessToken(any(), eq(user)))
                .thenReturn("new-access");

        when(jwtService.generateRefreshToken(any(), eq(user)))
                .thenReturn("new-refresh");

        // Act
        AuthResponse response = authService.refreshToken(token);

        // Assert
        assertNotNull(response);
        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());

        verify(refreshTokenRepository).delete(storedToken);
        verify(refreshTokenRepository).save(any());
    }
}

