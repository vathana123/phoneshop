package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.BrandDto;

public interface BrandService {
    BrandDto findById(Long id);
    BrandDto save(BrandDto brandDto);
    BrandDto update(Long id, BrandDto brandDto);
}
