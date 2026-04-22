package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.CategoryDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.CategoryType;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.CategoryMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.BrandRepository;
import com.backend.phoneshop.repository.CategoryRepository;
import com.backend.phoneshop.repository.CategoryTypeRepository;
import com.backend.phoneshop.service.CategoryService;
import com.backend.phoneshop.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final BrandRepository brandRepository;
    private final CategoryTypeRepository categoryTypeRepository;
    private final CategoryMapper mapper;

    @Override
    public PageResponse<CategoryDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<Category> page = repository.findAll(CategorySpecification.builder().filters(filters).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public CategoryDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Category.class, id)));
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category entity = mapper.toEntity(dto);
        entity.setBrand(findBrand(dto.brandId()));
        entity.setCategoryType(findCategoryType(dto.categoryTypeId()));
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto dto) {
        Category entity = repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Category.class, id));
        entity = mapper.mergeDto(dto, entity);
        entity.setBrand(findBrand(dto.brandId()));
        entity.setCategoryType(findCategoryType(dto.categoryTypeId()));
        return mapper
                .toDto(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Category.class, id)));
    }

    private Brand findBrand(Long id) {
        return brandRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(Brand.class, id));
    }

    private CategoryType findCategoryType(Long id) {
        return categoryTypeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(CategoryType.class, id));
    }
}
