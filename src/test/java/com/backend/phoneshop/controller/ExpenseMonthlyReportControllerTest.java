package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.ExpenseMonthlyReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseMonthlyReportController.class)
class ExpenseMonthlyReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseMonthlyReportService service;

    @Test
    @DisplayName("Should return expense monthly report")
    void getExpenseReport_ShouldReturnReport() throws Exception {

        ExpenseMonthlyReportDto dto1 = new ExpenseMonthlyReportDto(
                "2026-04",
                "iPhone 15",
                "NEW",
                "Phone",
                "Apple",
                "Smartphone",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(12000),
                2L
        );

        ExpenseMonthlyReportDto dto2 = new ExpenseMonthlyReportDto(
                "2026-04",
                "Galaxy S24",
                "NEW",
                "Phone",
                "Samsung",
                "Smartphone",
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(5000),
                1L
        );

        PageResponse<ExpenseMonthlyReportDto> response =
                PageResponse.<ExpenseMonthlyReportDto>builder()
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

        Mockito.when(service.getExpenseReport(
                        isNull(),
                        isNull(),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/expense_reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].productName").value("iPhone 15"))
                .andExpect(jsonPath("$.content[0].brandName").value("Apple"))
                .andExpect(jsonPath("$.content[0].totalExpenseAmount").value(12000))
                .andExpect(jsonPath("$.content[1].productName").value("Galaxy S24"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return expense report with date filters")
    void getExpenseReport_ShouldReturnFilteredReport() throws Exception {

        ExpenseMonthlyReportDto dto = new ExpenseMonthlyReportDto(
                "2026-04",
                "iPhone 15",
                "NEW",
                "Phone",
                "Apple",
                "Smartphone",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(12000),
                2L
        );

        PageResponse<ExpenseMonthlyReportDto> response =
                PageResponse.<ExpenseMonthlyReportDto>builder()
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

        Mockito.when(service.getExpenseReport(
                        eq(java.time.LocalDate.parse("2026-04-01")),
                        eq(java.time.LocalDate.parse("2026-04-30")),
                        any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/expense_reports")
                        .param("startDate", "2026-04-01")
                        .param("endDate", "2026-04-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].productName").value("iPhone 15"))
                .andExpect(jsonPath("$.content[0].month").value("2026-04"));
    }
}