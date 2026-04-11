package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.entities.Brand;

public class BrandMapper {
    public static Brand toBrand(BrandDto brandDto) {
        Brand brand = new Brand();
        brand.setName(brandDto.getName());
        return brand;
    }

    public static BrandDto toBrandDto(Brand brand) {
        BrandDto dto = new BrandDto();
        brand.setName(dto.getName());
        return dto;
    }
}
