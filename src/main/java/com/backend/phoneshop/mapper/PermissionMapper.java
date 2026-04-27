package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.PermissionDto;
import com.backend.phoneshop.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionDto toDto(Permission entity);

    @Mapping(target = "roles", ignore = true)
    Permission toEntity(PermissionDto dto);

    Set<PermissionDto> toDtoSet(Set<Permission> entities);

    @Mapping(target = "roles", ignore = true)
    Set<Permission> toEntitySet(Set<PermissionDto> dtos);
}
