package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.entities.CategoryType;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryTypeMapper {
    CategoryTypeMapper INSTANCE = Mappers.getMapper(CategoryTypeMapper.class);
    CategoryTypeDto toDto(CategoryType brand);
    CategoryType toEntity(CategoryTypeDto brandDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    CategoryType mergeDto(CategoryTypeDto dto, @MappingTarget CategoryType entity);
}
