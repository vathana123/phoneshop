package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReportDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.mapper.ExpenseMonthlyReportMapper;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.repository.ProductImportRepository;
import com.backend.phoneshop.service.ExpenseMonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseMonthlyReportServiceImpl implements ExpenseMonthlyReportService {
    private final ProductImportRepository repository;
    private final ExpenseMonthlyReportMapper mapper;

    @Override
    public PageResponse<ExpenseMonthlyReportDto> getExpenseReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return PageResponseMapper.toPageResponse(repository.getExpenseMonthlyReport(startDate, endDate, pageable), mapper::toExpenseMonthlyDto);
    }
}
