package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    PageResponse<BrandDto> findAll(String search, Pageable pageable);
    BrandDto findById(Long id);
    BrandDto save(BrandDto brandDto);
    BrandDto update(Long id, BrandDto brandDto);
    void delete(Long id);
}
