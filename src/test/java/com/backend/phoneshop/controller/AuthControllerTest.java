package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.request.AuthRequest;
import com.backend.phoneshop.dto.respone.AuthResponse;
import com.backend.phoneshop.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void login_ShouldReturnAuthResponse() throws Exception {

        // Arrange
        AuthRequest request = new AuthRequest(
                "admin",
                "123456"
        );

        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .accessTokenExpiry(3600)
                .tokenType("Bearer")
                .build();

        when(authService.login(any(AuthRequest.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.accessTokenExpiry").value(3600))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).login(any(AuthRequest.class));
    }

    @Test
    void refresh_ShouldReturnNewTokens() throws Exception {

        // Arrange
        String authorizationHeader = "Bearer refresh-token";

        AuthResponse response = AuthResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .accessTokenExpiry(3600)
                .tokenType("Bearer")
                .build();

        when(authService.refreshToken(eq(authorizationHeader)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/auth/refresh")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.accessTokenExpiry").value(3600))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));

        verify(authService).refreshToken(authorizationHeader);
    }

    @Test
    void logout_ShouldReturn200() throws Exception {

        // Act + Assert
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());
    }
}