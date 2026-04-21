package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.SaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SaleProductRepository extends JpaRepository<SaleProduct, Long>, JpaSpecificationExecutor<SaleProduct> {
}