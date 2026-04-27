package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.ProductImportDto;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.ProductImport;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.ProductImportMapper;
import com.backend.phoneshop.mapper.ProductMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.CategoryRepository;
import com.backend.phoneshop.repository.ProductImportRepository;
import com.backend.phoneshop.repository.ProductRepository;
import com.backend.phoneshop.service.ProductService;
import com.backend.phoneshop.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductImportRepository productImportRepository;
    private final ProductMapper mapper;
    private final ProductImportMapper productImportMapper;

    @Override
    public PageResponse<ProductDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<Product> page = repository.findAll(ProductSpecification.builder().filters(filters).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public ProductDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Product.class, id)));
    }

    @Override
    public ProductDto save(ProductDto dto) {
        Product entity = mapper.toEntity(dto);
        entity.setCategory(findCategory(dto.categoryId()));
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product entity = repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Product.class, id));
        entity = mapper
                .mergeDto(dto, entity);
        entity.setCategory(findCategory(dto.categoryId()));
        return mapper
                .toDto(repository.save(entity));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Product.class, id)));
    }

    @Override
    public ProductImportDto importProduct(ProductImportDto dto) {
        Product product = repository.findById(dto.productId())
                .orElseThrow(()->new ResourceNotFoundException(Product.class, dto.productId()));
        product.setAvailableUnit(dto.importUnit() + product.getAvailableUnit());
        product = repository.save(product);
        ProductImport entity = productImportMapper.toEntity(dto);
        entity.setProduct(product);;
        return productImportMapper.toDto(productImportRepository.save(entity));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(Category.class, id));
    }
}
