package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.PermissionDto;
import com.backend.phoneshop.dto.data.RoleDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.RoleService;
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

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService service;

    @Test
    @DisplayName("Should return all roles")
    void getAll_ShouldReturnRoles() throws Exception {

        PermissionDto permission = new PermissionDto(
                1L,
                "PRODUCT_READ",
                "PRODUCT"
        );

        RoleDto admin = RoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .permissions(Set.of(permission))
                .build();

        RoleDto staff = RoleDto.builder()
                .id(2L)
                .name("STAFF")
                .permissions(Set.of(permission))
                .build();

        PageResponse<RoleDto> response =
                PageResponse.<RoleDto>builder()
                        .content(List.of(admin, staff))
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

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.content[1].name").value("STAFF"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return role by id")
    void getById_ShouldReturnRole() throws Exception {

        PermissionDto permission = new PermissionDto(
                1L,
                "PRODUCT_READ",
                "PRODUCT"
        );

        RoleDto dto = RoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .permissions(Set.of(permission))
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.permissions.length()").value(1))
                .andExpect(jsonPath("$.permissions[0].name")
                        .value("PRODUCT_READ"));
    }

    @Test
    @DisplayName("Should save role")
    void save_ShouldCreateRole() throws Exception {

        PermissionDto permission = new PermissionDto(
                1L,
                "PRODUCT_READ",
                "PRODUCT"
        );

        RoleDto response = RoleDto.builder()
                .id(1L)
                .name("ADMIN")
                .permissions(Set.of(permission))
                .build();

        Mockito.when(service.save(any(RoleDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "ADMIN",
                    "permissions": [
                        {
                            "id": 1
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.permissions.length()").value(1));
    }

    @Test
    @DisplayName("Should validate save role request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": null,
                    "permissions": []
                }
                """;

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update role")
    void update_ShouldUpdateRole() throws Exception {

        PermissionDto permission = new PermissionDto(
                1L,
                "PRODUCT_WRITE",
                "PRODUCT"
        );

        RoleDto response = RoleDto.builder()
                .id(1L)
                .name("UPDATED_ADMIN")
                .permissions(Set.of(permission))
                .build();

        Mockito.when(service.update(eq(1L), any(RoleDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "UPDATED_ADMIN",
                    "permissions": [
                        {
                            "id": 1
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UPDATED_ADMIN"));
    }

    @Test
    @DisplayName("Should validate update role request")
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": null,
                    "permissions": []
                }
                """;

        mockMvc.perform(put("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete role")
    void delete_ShouldDeleteRole() throws Exception {

        Mockito.doNothing()
                .when(service)
                .delete(1L);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Role 1 has been deleted."));
    }
}