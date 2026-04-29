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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService service;

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(filters, pageable));
    }

    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid ProductDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ProductDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Product %s has been deleted.".formatted(id));
    }

    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @PostMapping("/import")
    public ResponseEntity<?> importProduct(@RequestBody @Valid ProductImportDto dto) {
        return ResponseEntity.ok(service.importProduct(dto));
    }
}
