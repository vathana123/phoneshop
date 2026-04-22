package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.ProductImportDto;
import com.backend.phoneshop.entity.ProductImport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImportMapper {

    @Mapping(source = "product.id", target = "productId")
    ProductImportDto toDto(ProductImport entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImport toEntity(ProductImportDto dto);
}
