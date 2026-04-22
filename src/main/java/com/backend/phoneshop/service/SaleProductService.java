package com.backend.phoneshop.service;

import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.dto.data.SaleProductDto;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface SaleProductService {

    PageResponse<SaleProductDto> findAll(Map<String, Object> filters, Pageable pageable);
    SaleProductDto findById(Long id);
    SaleProductDto save(SaleProductDto dto);
    void cancel(Long id);
}
