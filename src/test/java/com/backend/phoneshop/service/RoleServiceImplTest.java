package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.PermissionDto;
import com.backend.phoneshop.dto.data.RoleDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Permission;
import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.impl.RoleServiceImpl;
import com.backend.phoneshop.mapper.RoleMapper;
import com.backend.phoneshop.repository.PermissionRepository;
import com.backend.phoneshop.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository repository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleMapper mapper;

    @InjectMocks
    private RoleServiceImpl service;

    private Role role;
    private RoleDto dto;
    private Permission permission;
    private PermissionDto permissionDto;

    @BeforeEach
    void setUp() {
        permission = Permission.builder()
                .id(1L)
                .name("BRAND_READ")
                .build();

        permissionDto = new PermissionDto(1L, "BRAND_READ", "BRAND");

        role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .permissions(Set.of(permission))
                .build();

        dto = new RoleDto(
                1L,
                "ADMIN",
                Set.of(permissionDto)
        );
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        Map<String, Object> filters = Map.of("search", "admin");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> page = new PageImpl<>(List.of(role));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(role)).thenReturn(dto);

        PageResponse<RoleDto> response = service.findAll(filters, pageable);

        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).findAll(any(Specification.class), eq(pageable));
        verify(mapper).toDto(role);
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnRole_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(role));
        when(mapper.toDto(role)).thenReturn(dto);

        RoleDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("ADMIN", result.name());
    }

    @Test
    void shouldThrowException_whenRoleNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

// ===============================
// SAVE
// ===============================

    @Test
    void shouldSaveRole_withPermissions() {
        when(mapper.toEntity(dto)).thenReturn(role);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(repository.save(role)).thenReturn(role);
        when(mapper.toDto(role)).thenReturn(dto);

        RoleDto result = service.save(dto);

        assertNotNull(result);

        verify(permissionRepository).findById(1L);
        verify(repository).save(role);
        verify(mapper).toDto(role);
    }

    @Test
    void shouldSaveRole_withoutPermissions() {
        RoleDto dtoNoPermissions = new RoleDto(1L, "ADMIN", null);

        when(mapper.toEntity(dtoNoPermissions)).thenReturn(role);
        when(repository.save(role)).thenReturn(role);
        when(mapper.toDto(role)).thenReturn(dtoNoPermissions);

        RoleDto result = service.save(dtoNoPermissions);

        assertNotNull(result);

        verify(permissionRepository, never()).findById(any());
        verify(repository).save(role);
    }

    @Test
    void shouldThrowException_whenPermissionNotFound_onSave() {
        when(mapper.toEntity(dto)).thenReturn(role);
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.save(dto));

        verify(repository, never()).save(any());
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateRole_withPermissions() {
        Role merged = Role.builder().id(1L).name("UPDATED").build();
        RoleDto updatedDto = new RoleDto(1L, "UPDATED", Set.of(permissionDto));

        when(repository.findById(1L)).thenReturn(Optional.of(role));
        when(mapper.mergeDto(dto, role)).thenReturn(merged);
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        when(repository.save(merged)).thenReturn(merged);
        when(mapper.toDto(merged)).thenReturn(updatedDto);

        RoleDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("UPDATED", result.name());

        verify(repository).findById(1L);
        verify(permissionRepository).findById(1L);
        verify(repository).save(merged);
    }

    @Test
    void shouldUpdateRole_withoutPermissions() {
        RoleDto dtoNoPermissions = new RoleDto(1L, "UPDATED", null);

        when(repository.findById(1L)).thenReturn(Optional.of(role));
        when(mapper.mergeDto(dtoNoPermissions, role)).thenReturn(role);
        when(repository.save(role)).thenReturn(role);
        when(mapper.toDto(role)).thenReturn(dtoNoPermissions);

        RoleDto result = service.update(1L, dtoNoPermissions);

        assertNotNull(result);

        verify(permissionRepository, never()).findById(any());
        verify(repository).save(role);
    }

    @Test
    void shouldThrowException_whenRoleNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

    @Test
    void shouldThrowException_whenPermissionNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(role));
        when(mapper.mergeDto(dto, role)).thenReturn(role);
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, dto));
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteRole_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(role));

        service.delete(1L);

        verify(repository).delete(role);
    }

    @Test
    void shouldThrowException_whenDeleteRoleNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, never()).delete(any(Role.class));
    }
}
