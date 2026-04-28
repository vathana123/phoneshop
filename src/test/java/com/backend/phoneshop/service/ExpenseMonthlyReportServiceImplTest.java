package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReport;
import com.backend.phoneshop.dto.data.ExpenseMonthlyReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.impl.ExpenseMonthlyReportServiceImpl;
import com.backend.phoneshop.mapper.ExpenseMonthlyReportMapper;
import com.backend.phoneshop.repository.ProductImportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseMonthlyReportServiceImplTest {
    @Mock
    private ProductImportRepository repository;

    @Mock
    private ExpenseMonthlyReportMapper mapper;

    @InjectMocks
    private ExpenseMonthlyReportServiceImpl service;

// ===============================
// GET EXPENSE REPORT
// ===============================

    @Test
    void shouldReturnPageResponse_whenValidDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);

        ExpenseMonthlyReport report = new ExpenseMonthlyReport(
                "2025-01",
                1L,
                "iPhone",
                "NEW",
                10L,
                "Phone",
                100L,
                "Apple",
                1000L,
                "Electronics",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(10000),
                5L
        );

        ExpenseMonthlyReportDto dto = new ExpenseMonthlyReportDto(
                "2025-01",
                "iPhone",
                "NEW",
                "Phone",
                "Apple",
                "Electronics",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(10000),
                5L
        );

        Page<ExpenseMonthlyReport> page = new PageImpl<>(List.of(report));

        when(repository.getExpenseMonthlyReport(startDate, endDate, pageable))
                .thenReturn(page);

        when(mapper.toExpenseMonthlyDto(report)).thenReturn(dto);

        // Act
        PageResponse<ExpenseMonthlyReportDto> response =
                service.getExpenseReport(startDate, endDate, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("iPhone", response.content().get(0).productName());

        verify(repository).getExpenseMonthlyReport(startDate, endDate, pageable);
        verify(mapper).toExpenseMonthlyDto(report);
    }

    @Test
    void shouldReturnPageResponse_whenDatesAreNull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        ExpenseMonthlyReport report = new ExpenseMonthlyReport(
                "2025-01",
                1L,
                "iPhone",
                "NEW",
                10L,
                "Phone",
                100L,
                "Apple",
                1000L,
                "Electronics",
                BigDecimal.ONE,
                BigDecimal.TEN,
                1L
        );

        ExpenseMonthlyReportDto dto = new ExpenseMonthlyReportDto(
                "2025-01",
                "iPhone",
                "NEW",
                "Phone",
                "Apple",
                "Electronics",
                BigDecimal.ONE,
                BigDecimal.TEN,
                1L
        );

        Page<ExpenseMonthlyReport> page = new PageImpl<>(List.of(report));

        when(repository.getExpenseMonthlyReport(null, null, pageable))
                .thenReturn(page);

        when(mapper.toExpenseMonthlyDto(report)).thenReturn(dto);

        // Act
        PageResponse<ExpenseMonthlyReportDto> response =
                service.getExpenseReport(null, null, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).getExpenseMonthlyReport(null, null, pageable);
        verify(mapper).toExpenseMonthlyDto(report);
    }

    @Test
    void shouldReturnEmptyPage_whenNoData() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Page<ExpenseMonthlyReport> emptyPage = Page.empty();

        when(repository.getExpenseMonthlyReport(null, null, pageable))
                .thenReturn(emptyPage);

        // Act
        PageResponse<ExpenseMonthlyReportDto> response =
                service.getExpenseReport(null, null, pageable);

        // Assert
        assertNotNull(response);
        assertTrue(response.content().isEmpty());

        verify(repository).getExpenseMonthlyReport(null, null, pageable);
        verify(mapper, never()).toExpenseMonthlyDto(any());
    }
}
