package com.backend.phoneshop.impl;

import com.backend.phoneshop.entities.Brand;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repository;
    @Override
    public Brand save(Brand brand) {
        return repository.save(brand);
    }
}
