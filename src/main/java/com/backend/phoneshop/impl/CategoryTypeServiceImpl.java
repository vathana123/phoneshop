package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.entities.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.CategoryTypeMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.CategoryTypeRepository;
import com.backend.phoneshop.service.CategoryTypeService;
import com.backend.phoneshop.specification.SearchNameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryTypeServiceImpl implements CategoryTypeService {
    private final CategoryTypeRepository repository;
    private final CategoryTypeMapper mapper;

    @Override
    public PageResponse<CategoryTypeDto> findAll(String search, Pageable pageable) {
        Page<CategoryType> page = repository.findAll(SearchNameSpecification.<CategoryType>builder().search(search).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public CategoryTypeDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(CategoryType.class, id)));
    }

    @Override
    public CategoryTypeDto save(CategoryTypeDto brandDto) {
        return mapper.toDto(repository.save(mapper.toEntity(brandDto)));
    }

    @Override
    public CategoryTypeDto update(Long id, CategoryTypeDto brandDto) {
        return mapper
                .toDto(repository.save(mapper
                        .mergeDto(brandDto, repository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(CategoryType.class, id)))));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(CategoryType.class, id)));
    }
}
