package com.backend.phoneshop.mapper;

import com.backend.phoneshop.dto.data.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleProductReportMapper {

    // 🔹 SaleProductReport → DTO
    SaleProductReportDto toReportDto(SaleProductReport report);
    List<SaleProductReportDto> toReportDtoList(List<SaleProductReport> reports);

    // 🔹 Monthly Report → DTO
    SaleProductMonthlyReportDto toMonthlyDto(SaleProductMonthlyReport report);
    List<SaleProductMonthlyReportDto> toMonthlyDtoList(List<SaleProductMonthlyReport> reports);
}