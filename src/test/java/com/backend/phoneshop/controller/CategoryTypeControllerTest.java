package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.CategoryTypeDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.CategoryTypeService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryTypeController.class)
class CategoryTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryTypeService service;

    @Test
    @DisplayName("Should return all category types")
    void getAll_ShouldReturnCategoryTypes() throws Exception {

        CategoryTypeDto dto1 = CategoryTypeDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        CategoryTypeDto dto2 = CategoryTypeDto.builder()
                .id(2L)
                .name("Laptop")
                .build();

        PageResponse<CategoryTypeDto> response = PageResponse.<CategoryTypeDto>builder()
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

        mockMvc.perform(get("/category_types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Phone"))
                .andExpect(jsonPath("$.content[1].name").value("Laptop"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return category type by id")
    void getById_ShouldReturnCategoryType() throws Exception {

        CategoryTypeDto dto = CategoryTypeDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/category_types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @DisplayName("Should save category type")
    void save_ShouldCreateCategoryType() throws Exception {

        CategoryTypeDto response = CategoryTypeDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        Mockito.when(service.save(any(CategoryTypeDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Phone"
                }
                """;

        mockMvc.perform(post("/category_types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    @DisplayName("Should validate save request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/category_types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update category type")
    void update_ShouldUpdateCategoryType() throws Exception {

        CategoryTypeDto response = CategoryTypeDto.builder()
                .id(1L)
                .name("Updated Phone")
                .build();

        Mockito.when(service.update(eq(1L), any(CategoryTypeDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Updated Phone"
                }
                """;

        mockMvc.perform(put("/category_types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Phone"));
    }

    @Test
    @DisplayName("Should validate update request")
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(put("/category_types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete category type")
    void delete_ShouldDeleteCategoryType() throws Exception {

        Mockito.doNothing()
                .when(service)
                .delete(1L);

        mockMvc.perform(delete("/category_types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Category Type 1 has been deleted."));
    }
}