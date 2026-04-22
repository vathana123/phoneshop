package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.CategoryDto;
import com.backend.phoneshop.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
public class CategoryController extends BaseController<CategoryService, CategoryDto, Long>{
    public CategoryController(CategoryService service) {
        super(service, "Category");
    }
}
