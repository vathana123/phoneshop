package com.backend.phoneshop.impl;

import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.SaleProductMonthlyReportDto;
import com.backend.phoneshop.dto.data.SaleProductReportDto;
import com.backend.phoneshop.mapper.PageResponseMapper;
import com.backend.phoneshop.mapper.SaleProductReportMapper;
import com.backend.phoneshop.repository.SaleProductReportRepository;
import com.backend.phoneshop.service.SaleProductReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SaleProductReportServiceImpl implements SaleProductReportService {
    private final SaleProductReportRepository repository;
    private final SaleProductReportMapper mapper;
    @Override
    public PageResponse<SaleProductReportDto> getSaleProductReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return PageResponseMapper.toPageResponse(repository.getSaleProductReport(startDate, endDate, pageable), mapper::toReportDto);
    }

    @Override
    public PageResponse<SaleProductMonthlyReportDto> getSaleProductMonthlyReport(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return PageResponseMapper.toPageResponse(repository.getMonthlyReport(startDate, endDate, pageable), mapper::toMonthlyDto);
    }
}
