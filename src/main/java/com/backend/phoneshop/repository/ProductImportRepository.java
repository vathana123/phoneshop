package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.ProductImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductImportRepository extends JpaRepository<ProductImport, Long>, JpaSpecificationExecutor<ProductImport> {
}
