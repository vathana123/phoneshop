package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.BrandDto;
import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.service.BrandService;
import com.fasterxml.jackson.databind.ObjectMapper;// Jackson

// Spring MVC
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Static imports (VERY IMPORTANT)
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllBrands() throws Exception {
        // Arrange
        PageResponse<BrandDto> response = new PageResponse<>(
                List.of(new BrandDto(1L, "Apple")),
                0, 10, 1, 1,
                false, false, true, true
        );

        given(service.findAll(any(), any())).willReturn(response);

        // Act & Assert
        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Apple"));
    }

    @Test
    void shouldGetBrandById() throws Exception {
        // Arrange
        BrandDto dto = new BrandDto(1L, "Samsung");
        given(service.findById(1L)).willReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Samsung"));
    }

    @Test
    void shouldCreateBrand() throws Exception {
        // Arrange
        BrandDto dto = new BrandDto(null, "Xiaomi");
        BrandDto saved = new BrandDto(1L, "Xiaomi");

        given(service.save(any())).willReturn(saved);

        // Act & Assert
        mockMvc.perform(post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateBrand() throws Exception {
        // Arrange
        BrandDto dto = new BrandDto(null, "Updated");
        BrandDto updated = new BrandDto(1L, "Updated");

        given(service.update(eq(1L), any())).willReturn(updated);

        // Act & Assert
        mockMvc.perform(put("/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteBrand() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Brand 1 has been deleted."));
    }
}