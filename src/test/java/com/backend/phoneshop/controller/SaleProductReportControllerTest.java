package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.SaleProductReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleProductReportController.class)
class SaleProductReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleProductReportService service;

    @Test
    @DisplayName("Should return sale product report")
    void getSaleProductReport_ShouldReturnReport() throws Exception {

        SaleProductReportDto dto = SaleProductReportDto.builder()
                .productName("iPhone 15")
                .productUsedStatus("NEW")
                .categoryName("Phone")
                .brandName("Apple")
                .categoryTypeName("Smartphone")
                .soldAt(Timestamp.valueOf(LocalDateTime.of(2026, 4, 28, 10, 0)))
                .quantity(2)
                .saleAmount(BigDecimal.valueOf(2400))
                .discount(0.0)
                .build();

        PageResponse<SaleProductReportDto> response =
                PageResponse.<SaleProductReportDto>builder()
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

        Mockito.when(service.getSaleProductReport(
                        isNull(),
                        isNull(),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/sale_product_reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].productName")
                        .value("iPhone 15"))
                .andExpect(jsonPath("$.content[0].brandName")
                        .value("Apple"))
                .andExpect(jsonPath("$.content[0].quantity")
                        .value(2));
    }

    @Test
    @DisplayName("Should return filtered sale product report")
    void getSaleProductReport_ShouldReturnFilteredReport() throws Exception {

        SaleProductReportDto dto = SaleProductReportDto.builder()
                .productName("Galaxy S24")
                .productUsedStatus("NEW")
                .categoryName("Phone")
                .brandName("Samsung")
                .categoryTypeName("Smartphone")
                .soldAt(Timestamp.valueOf(LocalDateTime.of(2026, 4, 1, 10, 0)))
                .quantity(1)
                .saleAmount(BigDecimal.valueOf(1000))
                .discount(0.0)
                .build();

        PageResponse<SaleProductReportDto> response =
                PageResponse.<SaleProductReportDto>builder()
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

        Mockito.when(service.getSaleProductReport(
                        eq(java.time.LocalDate.parse("2026-04-01")),
                        eq(java.time.LocalDate.parse("2026-04-30")),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/sale_product_reports")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName")
                        .value("Galaxy S24"));
    }

    @Test
    @DisplayName("Should return monthly sale product report")
    void getSaleProductMonthlyReport_ShouldReturnMonthlyReport() throws Exception {

        SaleProductMonthlyReportDto dto =
                SaleProductMonthlyReportDto.builder()
                        .month("2026-04")
                        .productName("iPhone 15")
                        .productUsedStatus("NEW")
                        .categoryName("Phone")
                        .brandName("Apple")
                        .categoryTypeName("Smartphone")
                        .soleProductCount(BigDecimal.valueOf(10))
                        .totalPaymentAmount(BigDecimal.valueOf(12000))
                        .totalPaidAmount(BigDecimal.valueOf(12000))
                        .build();

        PageResponse<SaleProductMonthlyReportDto> response =
                PageResponse.<SaleProductMonthlyReportDto>builder()
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

        Mockito.when(service.getSaleProductMonthlyReport(
                        isNull(),
                        isNull(),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/sale_product_reports/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].month")
                        .value("2026-04"))
                .andExpect(jsonPath("$.content[0].productName")
                        .value("iPhone 15"))
                .andExpect(jsonPath("$.content[0].totalPaymentAmount")
                        .value(12000));
    }

    @Test
    @DisplayName("Should return filtered monthly sale product report")
    void getSaleProductMonthlyReport_ShouldReturnFilteredMonthlyReport() throws Exception {

        SaleProductMonthlyReportDto dto =
                SaleProductMonthlyReportDto.builder()
                        .month("2026-04")
                        .productName("Galaxy S24")
                        .productUsedStatus("NEW")
                        .categoryName("Phone")
                        .brandName("Samsung")
                        .categoryTypeName("Smartphone")
                        .soleProductCount(BigDecimal.valueOf(5))
                        .totalPaymentAmount(BigDecimal.valueOf(5000))
                        .totalPaidAmount(BigDecimal.valueOf(5000))
                        .build();

        PageResponse<SaleProductMonthlyReportDto> response =
                PageResponse.<SaleProductMonthlyReportDto>builder()
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

        Mockito.when(service.getSaleProductMonthlyReport(
                        eq(java.time.LocalDate.parse("2026-04-01")),
                        eq(java.time.LocalDate.parse("2026-04-30")),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/sale_product_reports/monthly")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productName")
                        .value("Galaxy S24"))
                .andExpect(jsonPath("$.content[0].month")
                        .value("2026-04"));
    }
}