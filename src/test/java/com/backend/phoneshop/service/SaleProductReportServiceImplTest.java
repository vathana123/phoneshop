package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.SaleProductMonthlyReport;
import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReport;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.impl.SaleProductReportServiceImpl;
import com.backend.phoneshop.mapper.SaleProductReportMapper;
import com.backend.phoneshop.repository.SaleProductReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleProductReportServiceImplTest {
    @Mock
    private SaleProductReportRepository repository;

    @Mock
    private SaleProductReportMapper mapper;

    @InjectMocks
    private SaleProductReportServiceImpl service;

// ===============================
// DETAIL REPORT
// ===============================

    @Test
    void shouldReturnDetailReport_whenValidRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);

        SaleProductReport report = new SaleProductReport(
                1L, 1L, 1L,
                "iPhone", "NEW",
                10L, "Phone",
                100L, "Apple",
                1000L, "Electronics",
                new Timestamp(System.currentTimeMillis()),
                2,
                BigDecimal.valueOf(1000),
                0.0
        );

        SaleProductReportDto dto = new SaleProductReportDto(
                "iPhone",
                "NEW",
                "Phone",
                "Apple",
                "Electronics",
                report.soldAt(),
                2,
                BigDecimal.valueOf(1000),
                0.0
        );

        Page<SaleProductReport> page = new PageImpl<>(List.of(report));

        when(repository.getSaleProductReport(startDate, endDate, pageable))
                .thenReturn(page);

        when(mapper.toReportDto(report)).thenReturn(dto);

        // Act
        PageResponse<SaleProductReportDto> response =
                service.getSaleProductReport(startDate, endDate, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("iPhone", response.content().get(0).productName());

        verify(repository).getSaleProductReport(startDate, endDate, pageable);
        verify(mapper).toReportDto(report);
    }

    @Test
    void shouldReturnDetailReport_whenDatesNull() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        SaleProductReport report = mock(SaleProductReport.class);
        SaleProductReportDto dto = mock(SaleProductReportDto.class);

        Page<SaleProductReport> page = new PageImpl<>(List.of(report));

        when(repository.getSaleProductReport(null, null, pageable))
                .thenReturn(page);

        when(mapper.toReportDto(report)).thenReturn(dto);

        // Act
        PageResponse<SaleProductReportDto> response =
                service.getSaleProductReport(null, null, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());

        verify(repository).getSaleProductReport(null, null, pageable);
        verify(mapper).toReportDto(report);
    }

    @Test
    void shouldReturnEmptyDetailReport_whenNoData() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.getSaleProductReport(null, null, pageable))
                .thenReturn(Page.empty());

        // Act
        PageResponse<SaleProductReportDto> response =
                service.getSaleProductReport(null, null, pageable);

        // Assert
        assertNotNull(response);
        assertTrue(response.content().isEmpty());

        verify(mapper, never()).toReportDto(any());
    }

// ===============================
// MONTHLY REPORT
// ===============================

    @Test
    void shouldReturnMonthlyReport_whenValidRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);

        SaleProductMonthlyReport report = new SaleProductMonthlyReport(
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
                BigDecimal.valueOf(20000),
                BigDecimal.valueOf(18000)
        );

        SaleProductMonthlyReportDto dto = new SaleProductMonthlyReportDto(
                "2025-01",
                "iPhone",
                "NEW",
                "Phone",
                "Apple",
                "Electronics",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20000),
                BigDecimal.valueOf(18000)
        );

        Page<SaleProductMonthlyReport> page = new PageImpl<>(List.of(report));

        when(repository.getMonthlyReport(startDate, endDate, pageable))
                .thenReturn(page);

        when(mapper.toMonthlyDto(report)).thenReturn(dto);

        // Act
        PageResponse<SaleProductMonthlyReportDto> response =
                service.getSaleProductMonthlyReport(startDate, endDate, pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals("2025-01", response.content().get(0).month());

        verify(repository).getMonthlyReport(startDate, endDate, pageable);
        verify(mapper).toMonthlyDto(report);
    }

    @Test
    void shouldReturnEmptyMonthlyReport_whenNoData() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.getMonthlyReport(null, null, pageable))
                .thenReturn(Page.empty());

        PageResponse<SaleProductMonthlyReportDto> response =
                service.getSaleProductMonthlyReport(null, null, pageable);

        assertNotNull(response);
        assertTrue(response.content().isEmpty());

        verify(mapper, never()).toMonthlyDto(any());
    }
}
