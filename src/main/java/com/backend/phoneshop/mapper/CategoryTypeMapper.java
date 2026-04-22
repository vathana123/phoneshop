package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.entity.CategoryType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryTypeMapper {
    CategoryTypeDto toDto(CategoryType brand);
    CategoryType toEntity(CategoryTypeDto brandDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    CategoryType mergeDto(CategoryTypeDto dto, @MappingTarget CategoryType entity);
}
