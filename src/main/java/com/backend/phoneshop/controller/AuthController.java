package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.request.AuthRequest;
import com.backend.phoneshop.dto.respone.AuthResponse;
import com.backend.phoneshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    // ===============================
    // LOGIN
    // ===============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    // ===============================
    // REFRESH TOKEN
    // ===============================
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        AuthResponse response = authService.refreshToken(authorizationHeader);

        return ResponseEntity.ok(response);
    }

    // ===============================
    // LOGOUT (optional endpoint)
    // ===============================
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Actual logout handled by Spring Security LogoutHandler
        return ResponseEntity.ok().build();
    }
}
