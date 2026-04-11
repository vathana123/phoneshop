package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.entities.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto brandDto);
    Brand mergeDto(BrandDto dto, @MappingTarget Brand entity);
}
