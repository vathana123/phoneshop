package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.PermissionDto;
import com.backend.phoneshop.dto.data.UserDto;
import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.entity.Permission;
import com.backend.phoneshop.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    // 🔹 INPUT → ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    User toEntity(UserInputDto dto);

    // 🔹 ENTITY → OUTPUT
    @Mapping(target = "roles", source = "roles") // maps to Set<UserRoleDto>
    UserDto toDto(User user);

    // 🔹 MERGE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    User merge(UserInputDto dto, @MappingTarget User entity);
}