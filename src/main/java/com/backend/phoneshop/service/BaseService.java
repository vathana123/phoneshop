package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BaseService<T, ID> {
    PageResponse<T> findAll(Map<String, Object> filters, Pageable pageable);
    T findById(ID id);
    T save(T dto);
    T update(ID id, T dto);
    void delete(ID id);
}
