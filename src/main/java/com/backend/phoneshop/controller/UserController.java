package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;

    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Map<String, Object> filters,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.findAll(filters, pageable));
    }

    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid UserInputDto dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody @Valid UserInputDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("hasAuthority('USER_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("User %s has been deleted.".formatted(id));
    }
}