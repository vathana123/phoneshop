package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long>, JpaSpecificationExecutor<SaleDetail> {
    List<SaleDetail> findBySaleProductIdIn(List<Long> saleProductIds);
    List<SaleDetail> findBySaleProductId(Long saleProductId);
}