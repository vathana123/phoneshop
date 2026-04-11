package com.backend.phoneshop.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Builder
@Data
public class ErrorResponse {
    private HttpStatus status;
    private String message;
}
