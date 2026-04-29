package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.service.CategoryTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("category_types")
public class CategoryTypeController {
    private final CategoryTypeService service;

    @PreAuthorize("hasAuthority('CATEGORY_TYPE_READ')")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(filters, pageable));
    }

    @PreAuthorize("hasAuthority('CATEGORY_TYPE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority('CATEGORY_TYPE_CREATE')")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid CategoryTypeDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PreAuthorize("hasAuthority('CATEGORY_TYPE_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid CategoryTypeDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAuthority('CATEGORY_TYPE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Category Type %s has been deleted.".formatted(id));
    }
}
