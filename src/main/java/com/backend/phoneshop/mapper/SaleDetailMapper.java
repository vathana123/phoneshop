package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.SaleDetailDto;
import com.backend.phoneshop.entity.SaleDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SaleDetailMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "saleProduct.id", target = "saleProductId")
    SaleDetailDto toDto(SaleDetail entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "saleProduct", ignore = true)
    SaleDetail toEntity(SaleDetailDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "saleProduct", ignore = true)
    SaleDetail mergeDto(SaleDetailDto dto, @MappingTarget SaleDetail entity);
}
