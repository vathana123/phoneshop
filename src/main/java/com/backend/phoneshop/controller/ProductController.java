package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.data.ProductImportDto;
import com.backend.phoneshop.service.ProductService;
import com.backend.phoneshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService service;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(filters, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid ProductDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Product %s has been deleted.".formatted(id));
    }
    @PostMapping("/import")
    public ResponseEntity<?> importProduct(@RequestBody @Valid ProductImportDto dto) {
        return ResponseEntity.ok(service.importProduct(dto));
    }
}
