package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.CategoryDto;
import com.backend.phoneshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("categories")
public class CategoryController extends BaseController<CategoryService, CategoryDto, Long>{
    public CategoryController(CategoryService service) {
        super(service, "Category");
    }
}
