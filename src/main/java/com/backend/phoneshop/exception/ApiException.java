package com.backend.phoneshop.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message); // 🔥 IMPORTANT
        this.status = status;
    }
}
