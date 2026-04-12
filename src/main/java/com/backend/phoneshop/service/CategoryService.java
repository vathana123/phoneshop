package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryDto;
import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CategoryService {
    PageResponse<CategoryDto> findAll(Map<String, Object> filters, Pageable pageable);
    CategoryDto findById(Long id);
    CategoryDto save(CategoryDto dto);
    CategoryDto update(Long id, CategoryDto dto);
    void delete(Long id);
}
