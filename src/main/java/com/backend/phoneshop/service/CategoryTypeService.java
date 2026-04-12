package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryTypeService {
    PageResponse<CategoryTypeDto> findAll(String search, Pageable pageable);
    CategoryTypeDto findById(Long id);
    CategoryTypeDto save(CategoryTypeDto categoryTypeDto);
    CategoryTypeDto update(Long id, CategoryTypeDto categoryTypeDto);
    void delete(Long id);
}
