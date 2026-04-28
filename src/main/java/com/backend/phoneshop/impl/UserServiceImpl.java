package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.UserDto;
import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.entity.Role;
import com.backend.phoneshop.entity.User;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.exception.ValidationException;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.mapper.UserMapper;
import com.backend.phoneshop.repository.RoleRepository;
import com.backend.phoneshop.repository.UserRepository;
import com.backend.phoneshop.service.UserService;
import com.backend.phoneshop.specification.SearchFilterSpecification;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<UserDto> findAll(Map<String, Object> filters, Pageable pageable) {
        Page<User> page = repository.findAll(SearchFilterSpecification.<User>builder().filters(filters).fields(List.of("name")).build(), pageable);
        return PageResponseMapper.toPageResponse(page, mapper::toDto);
    }

    @Override
    public UserDto findById(Long id) {
        return mapper
                .toDto(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(User.class, id)));
    }

    @Override
    public UserDto save(UserInputDto dto) {
        if (repository.existsByUsername(dto.username())){
            throw new ValidationException("This username is already taken!. Please try other username.");
        }
        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode("123456"));
        if (dto.roles() != null) {
            user.setRoles(dto.roles().stream().map(this::getRole).collect(Collectors.toSet()));
        }

        return mapper.toDto(repository.save(user));
    }

    @Override
    public UserDto update(Long id, UserInputDto dto) {
        User user = repository.findById(id).orElseThrow(()->new ResourceNotFoundException(User.class, id));
        user = mapper.merge(dto, user);
        user.setTokenVersion(user.getTokenVersion() + 1);
        if (dto.roles() != null) {
            user.setRoles(dto.roles().stream().map(this::getRole).collect(Collectors.toSet()));
        }

        return mapper.toDto(repository.save(user));
    }

    @Override
    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(()->new ResourceNotFoundException(User.class, id)));
    }

    @Nonnull
    private Role getRole(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, id));
    }
}
