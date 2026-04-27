package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ExpenseMonthlyReportService {
    PageResponse<ExpenseMonthlyReportDto> getExpenseReport(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
