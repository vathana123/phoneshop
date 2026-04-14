package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.ProductDto;
import com.backend.phoneshop.dto.ProductImportDto;

public interface ProductService extends BaseService<ProductDto, Long>{
    ProductImportDto importProduct(ProductImportDto dto);
}
