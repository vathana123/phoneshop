package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.service.BrandService;
import com.backend.phoneshop.specification.SearchFilterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    private final BrandRepository repository;
    private final BrandMapper mapper;

    @Override
    public PageResponse<BrandDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<Brand> page = repository.findAll(SearchFilterSpecification.<Brand>builder().filters(filters).fields(List.of("name")).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public BrandDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Brand.class, id)));
    }

    @Override
    public BrandDto save(BrandDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public BrandDto update(Long id, BrandDto dto) {
        return mapper
                .toDto(repository.save(mapper
                        .mergeDto(dto, repository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(Brand.class, id)))));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Brand.class, id)));
    }
}
