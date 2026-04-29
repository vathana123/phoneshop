package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.ProductDto;
import com.backend.phoneshop.dto.data.ProductImportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
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

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Test
    @DisplayName("Should return all products")
    void getAll_ShouldReturnProducts() throws Exception {

        ProductDto dto1 = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .usedStatus("NEW")
                .salePrice(BigDecimal.valueOf(1200))
                .categoryId(1L)
                .categoryName("Phone")
                .brand("Apple")
                .build();

        ProductDto dto2 = ProductDto.builder()
                .id(2L)
                .name("Galaxy S24")
                .usedStatus("NEW")
                .salePrice(BigDecimal.valueOf(1000))
                .categoryId(1L)
                .categoryName("Phone")
                .brand("Samsung")
                .build();

        PageResponse<ProductDto> response =
                PageResponse.<ProductDto>builder()
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

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("iPhone 15"))
                .andExpect(jsonPath("$.content[1].name").value("Galaxy S24"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return product by id")
    void getById_ShouldReturnProduct() throws Exception {

        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .usedStatus("NEW")
                .salePrice(BigDecimal.valueOf(1200))
                .categoryId(1L)
                .categoryName("Phone")
                .brand("Apple")
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"));
    }

    @Test
    @DisplayName("Should save product")
    void save_ShouldCreateProduct() throws Exception {

        ProductDto response = ProductDto.builder()
                .id(1L)
                .name("iPhone 15")
                .usedStatus("NEW")
                .salePrice(BigDecimal.valueOf(1200))
                .categoryId(1L)
                .categoryName("Phone")
                .brand("Apple")
                .build();

        Mockito.when(service.save(any(ProductDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "iPhone 15",
                    "usedStatus": "NEW",
                    "salePrice": 1200,
                    "categoryId": 1
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"));
    }

    @Test
    @DisplayName("Should validate save product request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": "",
                    "usedStatus": "",
                    "salePrice": null,
                    "categoryId": null
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update product")
    void update_ShouldUpdateProduct() throws Exception {

        ProductDto response = ProductDto.builder()
                .id(1L)
                .name("Updated iPhone 15")
                .usedStatus("USED")
                .usedQuality(95.0)
                .salePrice(BigDecimal.valueOf(1000))
                .categoryId(1L)
                .categoryName("Phone")
                .brand("Apple")
                .build();

        Mockito.when(service.update(eq(1L), any(ProductDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "name": "Updated iPhone 15",
                    "usedStatus": "USED",
                    "usedQuality": 95,
                    "salePrice": 1000,
                    "categoryId": 1
                }
                """;

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated iPhone 15"))
                .andExpect(jsonPath("$.usedStatus").value("USED"));
    }

    @Test
    @DisplayName("Should validate update product request")
    void update_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "name": "",
                    "usedStatus": "",
                    "usedQuality": 120,
                    "salePrice": -1,
                    "categoryId": null
                }
                """;

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete product")
    void delete_ShouldDeleteProduct() throws Exception {

        Mockito.doNothing()
                .when(service)
                .delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Product 1 has been deleted."));
    }

    @Test
    @DisplayName("Should import product")
    void importProduct_ShouldImportProduct() throws Exception {

        ProductImportDto response = ProductImportDto.builder()
                .id(1L)
                .importDate(new Date())
                .importUnit(10)
                .unitPrice(BigDecimal.valueOf(900))
                .productId(1L)
                .build();

        Mockito.when(service.importProduct(any(ProductImportDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "importDate": "2026-04-28T00:00:00.000+00:00",
                    "importUnit": 10,
                    "unitPrice": 900,
                    "productId": 1
                }
                """;

        mockMvc.perform(post("/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.importUnit").value(10))
                .andExpect(jsonPath("$.productId").value(1));
    }

    @Test
    @DisplayName("Should validate import product request")
    void importProduct_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "importDate": null,
                    "importUnit": 0,
                    "unitPrice": -1,
                    "productId": null
                }
                """;

        mockMvc.perform(post("/products/import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}