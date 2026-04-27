package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
}
