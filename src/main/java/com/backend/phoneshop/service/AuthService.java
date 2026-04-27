package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.request.AuthRequest;
import com.backend.phoneshop.dto.respone.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);
    AuthResponse refreshToken(String authorizationHeader);
}
