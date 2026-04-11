package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("brands")
public class BrandController {
    private final BrandService service;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody BrandDto brandDto) {
        return ResponseEntity.ok(service.save(brandDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BrandDto brandDto) {
        return ResponseEntity.ok(service.update(id,brandDto));
    }
}
