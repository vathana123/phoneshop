package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.SaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleProductRepository extends JpaRepository<SaleProduct, Long>, JpaSpecificationExecutor<SaleProduct> {
}