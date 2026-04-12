package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.CategoryDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ GET ALL
    @Test
    void shouldReturnPagedCategories() throws Exception {
        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        PageResponse<CategoryDto> response = PageResponse.<CategoryDto>builder()
                .content(List.of(dto))
                .build();

        when(service.findAll(anyMap(), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/categories")
                        .param("name", "phone")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Phone"));
    }

    // ✅ GET BY ID
    @Test
    void shouldReturnCategoryById() throws Exception {
        CategoryDto dto = CategoryDto.builder()
                .id(1L)
                .name("Phone")
                .build();

        when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    // ✅ POST
    @Test
    void shouldCreateCategory() throws Exception {
        CategoryDto dto = CategoryDto.builder()
                .name("Phone")
                .brandId(1L)
                .categoryTypeId(2L)
                .build();

        when(service.save(any(CategoryDto.class))).thenReturn(dto);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    // ✅ PUT
    @Test
    void shouldUpdateCategory() throws Exception {
        CategoryDto dto = CategoryDto.builder()
                .name("Updated Phone")
                .brandId(1L)
                .categoryTypeId(2L)
                .build();

        when(service.update(eq(1L), any(CategoryDto.class))).thenReturn(dto);

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Phone"));
    }

    // ✅ DELETE
    @Test
    void shouldDeleteCategory() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category 1 has been deleted."));
    }
}