package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.SaleProductDto;
import com.backend.phoneshop.entity.SaleProduct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = SaleDetailMapper.class)
public interface SaleProductMapper {

    SaleProductMapper INSTANCE = Mappers.getMapper(SaleProductMapper.class);

    SaleProductDto toDto(SaleProduct entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saleDetails", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    SaleProduct toEntity(SaleProductDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saleDetails", ignore = true)
    @Mapping(target = "soldAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    SaleProduct mergeDto(SaleProductDto dto, @MappingTarget SaleProduct entity);
}
