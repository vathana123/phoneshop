package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.categoryType.name", target = "categoryType")
    @Mapping(source = "category.brand.name", target = "brand")
    ProductDto toDto(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product mergeDto(ProductDto dto, @MappingTarget Product entity);
}
