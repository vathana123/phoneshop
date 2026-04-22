package com.backend.phoneshop.exception;

import com.backend.phoneshop.dto.respone.ResourceNotFoundResponse;
import com.backend.phoneshop.dto.respone.ValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResourceNotFoundResponse> handleApiException(ApiException e) {
        ResourceNotFoundResponse response = ResourceNotFoundResponse.builder()
                .status(e.getStatus())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        ValidationResponse response = ValidationResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .messages(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}
