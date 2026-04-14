package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.ProductImportDto;
import com.backend.phoneshop.entity.ProductImport;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductImportMapper {

    ProductImportMapper INSTANCE = Mappers.getMapper(ProductImportMapper.class);

    @Mapping(source = "product.id", target = "productId")
    ProductImportDto toDto(ProductImport entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImport toEntity(ProductImportDto dto);
}
