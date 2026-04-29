package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.CategoryDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.CategoryService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService service;

    @Test
    @DisplayName("Should return all categories")
    void getAll_ShouldReturnCategories() throws Exception {

        CategoryDto dto1 = CategoryDto.builder()
                .id(1L)
                .name("iPhone")
                .brandId(1L)
                .brandName("Apple")
                .categoryTypeId(1L)
                .categoryTypeName("Phone")
                .build();

        CategoryDto dto2 = CategoryDto.builder()
                .id(2L)
                .name("Galaxy")
                .brandId(2L)
                .brandName("Samsung")
                .categoryTypeId(1L)
                .categoryTypeName("Phone")
                .build();

        PageResponse<CategoryDto> response = PageResponse.<CategoryDto>builder()
                .content(List.of(dto1, dto2))
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

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("iPhone"))
                .andExpect(jsonPath("$.content[0].brandName").value("Apple"))
                .andExpect(jsonPath("$.content[1].name").value("Galaxy"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return category by id")
    void getById_ShouldReturnCategory() throws Exception {

        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("iPhone")
                .brandId(1L)
                .brandName("Apple")
                .categoryTypeId(1L)
                .categoryTypeName("Phone")
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.brandName").value("Apple"))
                .andExpect(jsonPath("$.categoryTypeName").value("Phone"));
    }

    @Test
    @DisplayName("Should save category")
    void save_ShouldCreateCategory() throws Exception {

        CategoryDto response = CategoryDto.builder()
                .id(1L)
                .name("iPhone")
                .brandId(1L)
                .brandName("Apple")
                .categoryTypeId(1L)
                .categoryTypeName("Phone")
                .build();

        Mockito.when(service.save(any(CategoryDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "iPhone",
                    "brandId": 1,
                    "categoryTypeId": 1
                }
                """;

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.categoryTypeId").value(1));
    }

    @Test
    @DisplayName("Should validate save request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": "",
                    "brandId": null,
                    "categoryTypeId": null
                }
                """;

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update category")
    void update_ShouldUpdateCategory() throws Exception {

        CategoryDto response = CategoryDto.builder()
                .id(1L)
                .name("Updated iPhone")
                .brandId(1L)
                .brandName("Apple")
                .categoryTypeId(1L)
                .categoryTypeName("Phone")
                .build();

        Mockito.when(service.update(eq(1L), any(CategoryDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Updated iPhone",
                    "brandId": 1,
                    "categoryTypeId": 1
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated iPhone"));
    }

    @Test
    @DisplayName("Should validate update request")
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": "",
                    "brandId": null,
                    "categoryTypeId": null
                }
                """;

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete category")
    void delete_ShouldDeleteCategory() throws Exception {

        Mockito.doNothing()
                .when(service)
                .delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Category 1 has been deleted."));
    }
}