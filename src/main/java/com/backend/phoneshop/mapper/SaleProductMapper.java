package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.SaleProductDto;
import com.backend.phoneshop.entity.SaleProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = SaleDetailMapper.class)
public interface SaleProductMapper {
    SaleProductDto toDto(SaleProduct entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saleDetails", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    SaleProduct toEntity(SaleProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saleDetails", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "canceledAt", ignore = true)
    SaleProduct mergeDto(SaleProductDto dto, @MappingTarget SaleProduct entity);
}
