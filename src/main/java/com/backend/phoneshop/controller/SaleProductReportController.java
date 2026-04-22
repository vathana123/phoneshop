package com.backend.phoneshop.controller;

import com.backend.phoneshop.service.SaleProductReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("sale_product_reports")
public class SaleProductReportController {
    private final SaleProductReportService service;
    @GetMapping
    public ResponseEntity<?> getSaleProductReport(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(service.getSaleProductReport(startDate, endDate, pageable));
    }
    @GetMapping("/monthly")
    public ResponseEntity<?> getSaleProductMonthlyReport(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(service.getSaleProductMonthlyReport(startDate, endDate, pageable));
    }
}
