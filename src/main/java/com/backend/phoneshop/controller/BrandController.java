package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.mapper.BrandMapper;
import com.backend.phoneshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("brands")
public class BrandController {
    private final BrandService service;
    @PostMapping
    public ResponseEntity<?> save(@RequestBody BrandDto brandDto) {
        return ResponseEntity.ok(BrandMapper.toBrandDto(service.save(BrandMapper.toBrand(brandDto))));
    }
}
