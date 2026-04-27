package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.data.ProductImportDto;

public interface ProductService extends BaseService<ProductDto, Long>{
    ProductImportDto importProduct(ProductImportDto dto);
}
