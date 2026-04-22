package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.mapper.SaleProductReportMapper;
import com.backend.phoneshop.repository.SaleProductReportRepository;
import com.backend.phoneshop.service.SaleProductReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleProductReportServiceImpl implements SaleProductReportService {
    private final SaleProductReportRepository repository;
    private final SaleProductReportMapper mapper;
    @Override
    public PageResponse<SaleProductReportDto> getSaleProductReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return PageResponseMapper.toPageResponse(repository.getSaleProductReport(startDate, endDate, pageable), mapper::toReportDto);
    }

    @Override
    public PageResponse<SaleProductMonthlyReportDto> getSaleProductMonthlyReport(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return PageResponseMapper.toPageResponse(repository.getMonthlyReport(startDate, endDate, pageable), mapper::toMonthlyDto);
    }
}
