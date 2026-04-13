package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.CategoryDto;
import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CategoryService extends BaseService<CategoryDto, Long> {
}
