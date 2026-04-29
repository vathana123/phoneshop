package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("brands")
public class BrandController {
    private final BrandService service;

    @PreAuthorize("hasAuthority('BRAND_READ')")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(filters, pageable));
    }

    @PreAuthorize("hasAuthority('BRAND_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority('BRAND_CREATE')")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid BrandDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PreAuthorize("hasAuthority('BRAND_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid BrandDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAuthority('BRAND_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Brand %s has been deleted.".formatted(id));
    }
}
