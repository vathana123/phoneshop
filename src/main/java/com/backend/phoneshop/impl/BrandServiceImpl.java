package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.entities.Brand;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.service.BrandService;
import com.backend.phoneshop.specification.SearchNameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repository;
    private final BrandMapper mapper;

    @Override
    public PageResponse<BrandDto> findAll(String search, Pageable pageable) {
        Page<Brand> page = repository.findAll(SearchNameSpecification.<Brand>builder().search(search).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public BrandDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Brand.class, id)));
    }

    @Override
    public BrandDto save(BrandDto brandDto) {
        return mapper.toDto(repository.save(mapper.toEntity(brandDto)));
    }

    @Override
    public BrandDto update(Long id, BrandDto brandDto) {
        return mapper
                .toDto(repository.save(mapper
                        .mergeDto(brandDto, repository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(Brand.class, id)))));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Brand.class, id)));
    }
}
