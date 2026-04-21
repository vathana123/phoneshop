package com.backend.phoneshop.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Builder
public record ValidationResponse(
    HttpStatus status,
    Map<String, String> messages
){}
