package com.backend.phoneshop.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record ResourceNotFoundResponse(
    HttpStatus status,
    String message
){}
