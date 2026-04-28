package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.PermissionDto;
import com.backend.phoneshop.dto.data.RoleDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Brand;
import com.backend.phoneshop.entity.Permission;
import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.mapper.RoleMapper;
import com.backend.phoneshop.repository.PermissionRepository;
import com.backend.phoneshop.repository.RoleRepository;
import com.backend.phoneshop.service.RoleService;
import com.backend.phoneshop.specification.SearchFilterSpecification;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper mapper;

    @Override
    public PageResponse<RoleDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<Role> page = repository.findAll(SearchFilterSpecification.<Role>builder().filters(filters).fields(List.of("name")).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public RoleDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Role.class, id)));
    }

    @Override
    public RoleDto save(RoleDto dto) {

        // 1. Map basic fields
        Role role = mapper.toEntity(dto);

        // 2. Resolve permissions from DB (🔥 important)
        if (dto.permissions() != null) {
            Set<Permission> permissions = dto.permissions().stream()
                    .map(this::getPermission)
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);
        }

        return mapper.toDto(repository.save(role));
    }

    @Override
    public RoleDto update(Long id, RoleDto dto) {

        // 1. Load existing role
        Role role = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, id));

        // 2. Merge basic fields (MapStruct)
        role = mapper.mergeDto(dto, role);

        // 3. Update permissions manually (🔥 required)
        if (dto.permissions() != null) {
            Set<Permission> permissions = dto.permissions().stream()
                    .map(this::getPermission)
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);
        }

        return mapper.toDto(repository.save(role));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(Role.class, id)));
    }

    @Nonnull
    private Permission getPermission(PermissionDto p) {
        return permissionRepository.findById(p.id())
                .orElseThrow(() -> new ResourceNotFoundException(Permission.class, p.id()));
    }
}
