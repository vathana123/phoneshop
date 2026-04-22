package com.backend.phoneshop.dto.respone;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast
) {}
