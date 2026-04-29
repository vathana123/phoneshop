package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.UserDto;
import com.backend.phoneshop.dto.data.UserInputDto;
import com.backend.phoneshop.dto.data.UserRoleDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;

    @Test
    @DisplayName("Should return all users")
    void getAll_ShouldReturnUsers() throws Exception {

        UserRoleDto adminRole = UserRoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("Vathana")
                .username("vathana")
                .roles(Set.of(adminRole))
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("Dara")
                .username("dara")
                .roles(Set.of(adminRole))
                .build();

        PageResponse<UserDto> response =
                PageResponse.<UserDto>builder()
                        .content(List.of(user1, user2))
                        .page(0)
                        .size(10)
                        .totalElements(2)
                        .totalPages(1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .isFirst(true)
                        .isLast(true)
                        .build();

        Mockito.when(service.findAll(anyMap(), any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Vathana"))
                .andExpect(jsonPath("$.content[1].username").value("dara"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return user by id")
    void getById_ShouldReturnUser() throws Exception {

        UserRoleDto adminRole = UserRoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        UserDto dto = UserDto.builder()
                .id(1L)
                .name("Vathana")
                .username("vathana")
                .roles(Set.of(adminRole))
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vathana"))
                .andExpect(jsonPath("$.username").value("vathana"))
                .andExpect(jsonPath("$.roles.length()").value(1))
                .andExpect(jsonPath("$.roles[0].name").value("ADMIN"));
    }

    @Test
    @DisplayName("Should save user")
    void save_ShouldCreateUser() throws Exception {

        UserRoleDto adminRole = UserRoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        UserDto response = UserDto.builder()
                .id(1L)
                .name("Vathana")
                .username("vathana")
                .roles(Set.of(adminRole))
                .build();

        Mockito.when(service.save(any(UserInputDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Vathana",
                    "username": "vathana",
                    "roles": [1]
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Vathana"))
                .andExpect(jsonPath("$.username").value("vathana"));
    }

    @Test
    @DisplayName("Should validate save user request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": null,
                    "username": null,
                    "roles": []
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update user")
    void update_ShouldUpdateUser() throws Exception {

        UserRoleDto adminRole = UserRoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        UserDto response = UserDto.builder()
                .id(1L)
                .name("Updated Vathana")
                .username("updated_vathana")
                .roles(Set.of(adminRole))
                .build();

        Mockito.when(service.update(eq(1L), any(UserInputDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Updated Vathana",
                    "username": "updated_vathana",
                    "roles": [1]
                }
                """;

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Vathana"))
                .andExpect(jsonPath("$.username").value("updated_vathana"));
    }

    @Test
    @DisplayName("Should validate update user request")
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": null,
                    "username": null,
                    "roles": []
                }
                """;

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete user")
    void delete_ShouldDeleteUser() throws Exception {

        Mockito.doNothing()
                .when(service)
                .delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("User 1 has been deleted."));
    }
}