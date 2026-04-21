package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.SaleDetailDto;
import com.backend.phoneshop.dto.SaleProductDto;
import com.backend.phoneshop.entity.SaleProduct;
import com.backend.phoneshop.exception.GlobalExceptionHandler;
import com.backend.phoneshop.exception.ResourceNotFoundException;
import com.backend.phoneshop.service.SaleProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleProductController.class)
@Import(GlobalExceptionHandler.class)
class SaleProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SaleProductService service;

    @Test
    void save_shouldReturnOk_whenRequestIsValid() throws Exception {
        SaleProductDto request = validRequest();
        SaleProductDto response = SaleProductDto.builder()
                .id(1L)
                .saleDetails(request.saleDetails())
                .totalAmount(new BigDecimal("180.00"))
                .paymentAmount(new BigDecimal("171.00"))
                .paidAmount(new BigDecimal("200.00"))
                .discount(5.0)
                .build();

        when(service.save(any(SaleProductDto.class))).thenReturn(response);

        mockMvc.perform(post("/sale_products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentAmount").value(171.00));
    }

    @Test
    void save_shouldReturnBadRequest_whenNestedSaleDetailIsInvalid() throws Exception {
        SaleProductDto request = SaleProductDto.builder()
                .saleDetails(List.of(SaleDetailDto.builder()
                        .quantity(0)
                        .saleAmount(new BigDecimal("-1.00"))
                        .build()))
                .paidAmount(new BigDecimal("10.00"))
                .discount(0.0)
                .build();

        mockMvc.perform(post("/sale_products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(content().string(containsString("saleDetails[0].productId")))
                .andExpect(content().string(containsString("Product ID is required")))
                .andExpect(content().string(containsString("Quantity must be greater than 0")))
                .andExpect(content().string(containsString("Sale amount must not be negative")));
    }

    @Test
    void update_shouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(put("/sale_products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.message").value("Updating sale products is not supported."));

        verifyNoInteractions(service);
    }

    @Test
    void getById_shouldReturnNotFound_whenServiceThrowsResourceNotFoundException() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException(SaleProduct.class, 99L));

        mockMvc.perform(get("/sale_products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SaleProduct with ID 99 not found."));
    }

    private SaleProductDto validRequest() {
        return SaleProductDto.builder()
                .saleDetails(List.of(SaleDetailDto.builder()
                        .productId(1L)
                        .quantity(2)
                        .saleAmount(new BigDecimal("100.00"))
                        .discount(10.0)
                        .build()))
                .paidAmount(new BigDecimal("200.00"))
                .discount(5.0)
                .build();
    }
}
