package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.UserDto;
import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.dto.data.UserRoleDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.exception.ValidationException;
import com.backend.phoneshop.impl.UserServiceImpl;
import com.backend.phoneshop.mapper.UserMapper;
import com.backend.phoneshop.repository.RoleRepository;
import com.backend.phoneshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserDto dto;
    private UserInputDto inputDto;
    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1L).name("ADMIN").build();

        user = User.builder()
                .id(1L)
                .name("Vathana")
                .username("vathana")
                .tokenVersion(0)
                .build();

        dto = new UserDto(
                1L,
                "Vathana",
                "vathana",
                Set.of(new UserRoleDto(1L, "ADMIN"))
        );

        inputDto = new UserInputDto(
                "Vathana",
                "vathana",
                Set.of(1L)
        );
    }

// ===============================
// FIND ALL
// ===============================

    @Test
    void shouldReturnPageResponse_whenFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.toDto(user)).thenReturn(dto);

        PageResponse<UserDto> response = service.findAll(Map.of(), pageable);

        assertNotNull(response);
        assertEquals(1, response.content().size());
    }

// ===============================
// FIND BY ID
// ===============================

    @Test
    void shouldReturnUser_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        UserDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("vathana", result.username());
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(1L));
    }

// ===============================
// SAVE
// ===============================

    @Test
    void shouldSaveUser_whenValid() {
        when(repository.existsByUsername("vathana")).thenReturn(false);
        when(mapper.toEntity(inputDto)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(dto);

        UserDto result = service.save(inputDto);

        assertNotNull(result);

        verify(passwordEncoder).encode("123456");
        verify(roleRepository).findById(1L);
        verify(repository).save(user);
    }

    @Test
    void shouldThrowException_whenUsernameExists() {
        when(repository.existsByUsername("vathana")).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> service.save(inputDto));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenRoleNotFound_onSave() {
        when(repository.existsByUsername("vathana")).thenReturn(false);
        when(mapper.toEntity(inputDto)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.save(inputDto));
    }

// ===============================
// UPDATE
// ===============================

    @Test
    void shouldUpdateUser_whenValid() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.merge(inputDto, user)).thenReturn(user);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(dto);

        UserDto result = service.update(1L, inputDto);

        assertNotNull(result);
        assertEquals(1, user.getTokenVersion()); // incremented

        verify(repository).save(user);
    }

    @Test
    void shouldThrowException_whenUserNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, inputDto));
    }

    @Test
    void shouldThrowException_whenRoleNotFound_onUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.merge(inputDto, user)).thenReturn(user);
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, inputDto));
    }

// ===============================
// DELETE
// ===============================

    @Test
    void shouldDeleteUser_whenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(1L);

        verify(repository).delete(user);
    }

    @Test
    void shouldThrowException_whenDeleteUserNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));

        verify(repository, never()).delete(any(User.class));
    }
}
