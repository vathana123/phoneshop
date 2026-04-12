package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryTypeService {
    PageResponse<CategoryTypeDto> findAll(String search, Pageable pageable);
    CategoryTypeDto findById(Long id);
    CategoryTypeDto save(CategoryTypeDto dto);
    CategoryTypeDto update(Long id, CategoryTypeDto dto);
    void delete(Long id);
}
