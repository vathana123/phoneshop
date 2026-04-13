package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("brands")
public class BrandController extends BaseController<BrandService,  BrandDto, Long> {
    public BrandController(BrandService service) {
        super(service, "Brand");
    }
}
