package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.entities.Brand;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repository;

    @Override
    public BrandDto findById(Long id) {
        return BrandMapper.INSTANCE.toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Brand", id)));
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        return BrandMapper.INSTANCE.toDto(repository.save(BrandMapper.INSTANCE.toEntity(brandDto)));
    }

    @Override
    public BrandDto update(Long id, BrandDto brandDto) {
        BrandDto oldBrand = findById(id);
        oldBrand.setName(brandDto.getName());
        return BrandMapper.INSTANCE.toDto(repository.save(BrandMapper.INSTANCE.toEntity(oldBrand)));
    }
}
