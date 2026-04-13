package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.PageResponse;
import com.backend.phoneshop.dto.ProductDto;
import com.backend.phoneshop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnPagedProducts() throws Exception {
        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .salePrice(new BigDecimal("999.99"))
                .availableUnit(5)
                .categoryId(10L)
                .build();

        PageResponse<ProductDto> response = PageResponse.<ProductDto>builder()
                .content(List.of(dto))
                .build();

        when(service.findAll(anyMap(), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/products")
                        .param("name", "iphone")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("iPhone 15"));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .build();

        when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone 15"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("iPhone 15")
                .salePrice(new BigDecimal("999.99"))
                .availableUnit(5)
                .categoryId(10L)
                .build();

        ProductDto saved = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .salePrice(new BigDecimal("999.99"))
                .availableUnit(5)
                .categoryId(10L)
                .build();

        when(service.save(any(ProductDto.class))).thenReturn(saved);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductDto dto = ProductDto.builder()
                .name("Updated Phone")
                .salePrice(new BigDecimal("899.99"))
                .availableUnit(3)
                .categoryId(10L)
                .build();

        ProductDto updated = ProductDto.builder()
                .id(1L)
                .name("Updated Phone")
                .salePrice(new BigDecimal("899.99"))
                .availableUnit(3)
                .categoryId(10L)
                .build();

        when(service.update(eq(1L), any(ProductDto.class))).thenReturn(updated);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Phone"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product 1 has been deleted."));
    }
}
