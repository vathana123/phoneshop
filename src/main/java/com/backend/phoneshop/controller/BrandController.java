package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.service.BrandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("brands")
public class BrandController extends BaseController<BrandService,  BrandDto, Long> {
    public BrandController(BrandService service) {
        super(service, "Brand");
    }
}
