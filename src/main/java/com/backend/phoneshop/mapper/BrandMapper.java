package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.entity.Brand;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto brandDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Brand mergeDto(BrandDto dto, @MappingTarget Brand entity);
}
