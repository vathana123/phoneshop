package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.UserDto;
import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserService {
    PageResponse<UserDto> findAll(Map<String, Object> filters, Pageable pageable);
    UserDto findById(Long id);
    UserDto save(UserInputDto dto);
    UserDto update(Long id, UserInputDto dto);
    void delete(Long id);
}
