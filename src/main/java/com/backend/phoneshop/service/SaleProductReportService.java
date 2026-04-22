package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface SaleProductReportService {
    PageResponse<SaleProductReportDto> getSaleProductReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    PageResponse<SaleProductMonthlyReportDto> getSaleProductMonthlyReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
