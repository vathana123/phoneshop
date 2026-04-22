package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.entity.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto brandDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Brand mergeDto(BrandDto dto, @MappingTarget Brand entity);
}
