package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SaleProductReportService {
    PageResponse<SaleProductReportDto> getSaleProductReport(LocalDate startDate, LocalDate endDate, Pageable pageable);
    PageResponse<SaleProductMonthlyReportDto> getSaleProductMonthlyReport(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
