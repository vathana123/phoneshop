package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.SaleProductDto;
import com.backend.phoneshop.exception.ApiException;
import com.backend.phoneshop.service.SaleProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sale_products")
public class SaleProductController extends BaseController<SaleProductService, SaleProductDto, Long> {
    public SaleProductController(SaleProductService service) {
        super(service, "Sale Product");
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid SaleProductDto dto) {
        throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED, "Updating sale products is not supported.");
    }
}
