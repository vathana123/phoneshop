package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReport;
import com.backend.phoneshop.dto.data.ExpenseMonthlyReportDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMonthlyReportMapper {
    // 🔹 Monthly Report → DTO
    ExpenseMonthlyReportDto toExpenseMonthlyDto(ExpenseMonthlyReport report);
    List<ExpenseMonthlyReportDto> toExpenseMonthlyDtoList(List<ExpenseMonthlyReport> reports);
}