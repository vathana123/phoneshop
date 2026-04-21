package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.ProductDto;
import com.backend.phoneshop.dto.ProductImportDto;
import com.backend.phoneshop.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("products")
public class ProductController extends BaseController<ProductService, ProductDto, Long>{
    public ProductController(ProductService service) {
        super(service, "Product");
    }

    @PostMapping("/import")
    public ResponseEntity<?> importProduct(@RequestBody @Valid ProductImportDto dto) {
        return ResponseEntity.ok(service.importProduct(dto));
    }
}
