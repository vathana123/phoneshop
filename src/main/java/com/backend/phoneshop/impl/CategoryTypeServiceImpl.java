package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.CategoryTypeMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.CategoryTypeRepository;
import com.backend.phoneshop.service.CategoryTypeService;
import com.backend.phoneshop.specification.SearchFilterSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryTypeServiceImpl implements CategoryTypeService {
    private final CategoryTypeRepository repository;
    private final CategoryTypeMapper mapper;

    @Override
    public PageResponse<CategoryTypeDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<CategoryType> page = repository.findAll(SearchFilterSpecification.<CategoryType>builder().filters(filters).fields(List.of("name")).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public CategoryTypeDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(CategoryType.class, id)));
    }

    @Override
    public CategoryTypeDto save(CategoryTypeDto dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public CategoryTypeDto update(Long id, CategoryTypeDto dto) {
        return mapper
                .toDto(repository.save(mapper
                        .mergeDto(dto, repository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(CategoryType.class, id)))));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(CategoryType.class, id)));
    }
}
