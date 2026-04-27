package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.RoleDto;
import com.backend.phoneshop.dto.data.UserRoleDto;
import com.backend.phoneshop.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public interface RoleMapper {
    // 🔹 Full Role → RoleDto (with permissions)
    RoleDto toDto(Role role);

    // 🔹 Role → UserRoleDto (NO permissions)
    UserRoleDto toUserRoleDto(Role role);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role mergeDto(RoleDto dto, @MappingTarget Role entity);
}