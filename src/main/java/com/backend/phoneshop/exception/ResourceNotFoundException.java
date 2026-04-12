package com.backend.phoneshop.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(Class<?> clazz, Object id) {
        super(
                HttpStatus.NOT_FOUND,
                "%s with ID %s not found.".formatted(clazz.getSimpleName(), id)
        );
    }
}
