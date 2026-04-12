package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryTypeRepository extends JpaRepository<CategoryType, Long>, JpaSpecificationExecutor<CategoryType> {
}
