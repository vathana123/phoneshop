package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.SaleDetailDto;
import com.backend.phoneshop.dto.data.SaleProductDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.SaleProductService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleProductController.class)
class SaleProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleProductService service;

    @Test
    @DisplayName("Should return all sale products")
    void getAll_ShouldReturnSaleProducts() throws Exception {

        SaleDetailDto detail = SaleDetailDto.builder()
                .id(1L)
                .productId(1L)
                .productName("iPhone 15")
                .quantity(2)
                .saleAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        SaleProductDto dto = SaleProductDto.builder()
                .id(1L)
                .saleDetails(List.of(detail))
                .totalAmount(BigDecimal.valueOf(2400))
                .paymentAmount(BigDecimal.valueOf(2400))
                .paidAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        PageResponse<SaleProductDto> response =
                PageResponse.<SaleProductDto>builder()
                        .content(List.of(dto))
                        .page(0)
                        .size(10)
                        .totalElements(1)
                        .totalPages(1)
                        .hasNext(false)
                        .hasPrevious(false)
                        .isFirst(true)
                        .isLast(true)
                        .build();

        Mockito.when(service.findAll(anyMap(), any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/sale_products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].totalAmount").value(2400));
    }

    @Test
    @DisplayName("Should return sale product by id")
    void getById_ShouldReturnSaleProduct() throws Exception {

        SaleDetailDto detail = SaleDetailDto.builder()
                .id(1L)
                .productId(1L)
                .productName("iPhone 15")
                .quantity(2)
                .saleAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        SaleProductDto dto = SaleProductDto.builder()
                .id(1L)
                .saleDetails(List.of(detail))
                .totalAmount(BigDecimal.valueOf(2400))
                .paymentAmount(BigDecimal.valueOf(2400))
                .paidAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        Mockito.when(service.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/sale_products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.saleDetails.length()").value(1))
                .andExpect(jsonPath("$.saleDetails[0].productName")
                        .value("iPhone 15"));
    }

    @Test
    @DisplayName("Should save sale product")
    void save_ShouldCreateSaleProduct() throws Exception {

        SaleDetailDto detail = SaleDetailDto.builder()
                .id(1L)
                .productId(1L)
                .productName("iPhone 15")
                .quantity(2)
                .saleAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        SaleProductDto response = SaleProductDto.builder()
                .id(1L)
                .saleDetails(List.of(detail))
                .totalAmount(BigDecimal.valueOf(2400))
                .paymentAmount(BigDecimal.valueOf(2400))
                .paidAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        Mockito.when(service.save(any(SaleProductDto.class)))
                .thenReturn(response);

        String request = """
                {
                    "saleDetails": [
                        {
                            "productId": 1,
                            "quantity": 2,
                            "saleAmount": 2400,
                            "discount": 0
                        }
                    ],
                    "paidAmount": 2400,
                    "discount": 0
                }
                """;

        mockMvc.perform(post("/sale_products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.saleDetails.length()").value(1))
                .andExpect(jsonPath("$.paidAmount").value(2400));
    }

    @Test
    @DisplayName("Should validate save sale product request")
    void save_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        String request = """
                {
                    "saleDetails": [],
                    "paidAmount": -1,
                    "discount": 120
                }
                """;

        mockMvc.perform(post("/sale_products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate nested sale detail request")
    void save_ShouldReturnBadRequest_WhenInvalidSaleDetail() throws Exception {

        String request = """
                {
                    "saleDetails": [
                        {
                            "productId": null,
                            "quantity": 0,
                            "saleAmount": -1,
                            "discount": 120
                        }
                    ],
                    "paidAmount": 100
                }
                """;

        mockMvc.perform(post("/sale_products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should cancel sale product")
    void cancel_ShouldCancelSaleProduct() throws Exception {

        Mockito.doNothing()
                .when(service)
                .cancel(1L);

        mockMvc.perform(put("/sale_products/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Sale Product 1 has been Canceled."));
    }
}