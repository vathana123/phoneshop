package com.backend.phoneshop.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
public record ErrorResponse (
    HttpStatus status,
    String message
){}
