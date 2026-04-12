package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.CategoryTypeDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.service.CategoryTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryTypeController.class)
public class CategoryTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryTypeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllCategoryTypes() throws Exception {
        // Arrange
        PageResponse<CategoryTypeDto> response = new PageResponse<>(
                List.of(new CategoryTypeDto(1L, "Charger")),
                0, 10, 1, 1,
                false, false, true, true
        );

        given(service.findAll(any(), any())).willReturn(response);

        // Act & Assert
        mockMvc.perform(get("/category_types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Charger"));
    }

    @Test
    void shouldGetCategoryTypeById() throws Exception {
        // Arrange
        CategoryTypeDto dto = new CategoryTypeDto(1L, "Tablet");
        given(service.findById(1L)).willReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/category_types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tablet"));
    }

    @Test
    void shouldCreateCategoryType() throws Exception {
        // Arrange
        CategoryTypeDto dto = new CategoryTypeDto(null, "Smart Phone");
        CategoryTypeDto saved = new CategoryTypeDto(1L, "Smart Phone");

        given(service.save(any())).willReturn(saved);

        // Act & Assert
        mockMvc.perform(post("/category_types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateCategoryType() throws Exception {
        // Arrange
        CategoryTypeDto dto = new CategoryTypeDto(null, "Updated");
        CategoryTypeDto updated = new CategoryTypeDto(1L, "Updated");

        given(service.update(eq(1L), any())).willReturn(updated);

        // Act & Assert
        mockMvc.perform(put("/category_types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteCategoryType() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/category_types/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("CategoryType 1 has been deleted."));
    }
}
