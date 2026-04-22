package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.respone.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PageResponseMapper {

    public static <T, D> PageResponse<D> toPageResponse(Page<T> page, Function<T, D> mapper) {
        return new PageResponse<>(
                page.get()
                        .map(mapper)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T, D> PageResponse<D> toPageResponse(Page<T> page, List<D> dtoList) {
        return new PageResponse<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious(),
                page.isFirst(),
                page.isLast()
        );
    }
}
