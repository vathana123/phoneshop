package com.backend.phoneshop.controller;

import com.backend.phoneshop.service.ExpenseMonthlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RestController
@RequestMapping("expense_reports")
public class ExpenseMonthlyReportController {
    private final ExpenseMonthlyReportService service;

    @GetMapping
    public ResponseEntity<?> getExpenseReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(service.getExpenseReport(startDate, endDate, pageable));
    }
}
