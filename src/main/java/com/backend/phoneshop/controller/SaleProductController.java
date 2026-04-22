package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.SaleProductDto;
import com.backend.phoneshop.service.SaleProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("sale_products")
public class SaleProductController {
    private final SaleProductService service;

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
    public ResponseEntity<?> save(@RequestBody @Valid SaleProductDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.ok("Sale Product %s has been Canceled.".formatted(id));
    }
}
