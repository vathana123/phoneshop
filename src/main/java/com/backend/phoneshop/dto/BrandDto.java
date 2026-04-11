package com.backend.phoneshop.dto;

import com.backend.phoneshop.entities.Brand;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandDto {
    private Long id;
    private String name;
}
