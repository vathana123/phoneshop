package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.ProductDto;
import com.backend.phoneshop.service.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductController extends BaseController<ProductService, ProductDto, Long>{
    public ProductController(ProductService service) {
        super(service, "Product");
    }
}
