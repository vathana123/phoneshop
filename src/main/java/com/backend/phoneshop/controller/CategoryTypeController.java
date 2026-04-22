package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.service.CategoryTypeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("category_types")
public class CategoryTypeController extends BaseController<CategoryTypeService, CategoryTypeDto, Long>{
    public CategoryTypeController(CategoryTypeService service) {
        super(service, "Category Type");
    }
}
