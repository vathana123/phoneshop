package com.backend.phoneshop.repository;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReport;
import com.backend.phoneshop.entity.ProductImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ProductImportRepository extends JpaRepository<ProductImport, Long>, JpaSpecificationExecutor<ProductImport> {
    @Query(value = """
    SELECT
        DATE_FORMAT(pi.import_date, '%Y-%m') AS month,

        p.id AS productId,
        p.name AS productName,
        p.used_status AS productUsedStatus,

        c.id AS categoryId,
        c.name AS categoryName,

        b.id AS brandId,
        b.name AS brandName,

        ct.id AS categoryTypeId,
        ct.name AS categoryTypeName,

        SUM(pi.import_unit) AS importProductCount,
        SUM(pi.import_unit * pi.unit_price) AS totalExpenseAmount,
        COUNT(pi.id) AS importCount

    FROM product_imports pi
    JOIN products p ON p.id = pi.product_id
    JOIN categories c ON c.id = p.category_id
    JOIN brands b ON b.id = c.brand_id
    JOIN category_types ct ON ct.id = c.category_type_id

    WHERE pi.deleted_at IS NULL
      AND (:startDate IS NULL OR pi.import_date >= :startDate)
      AND (:endDate IS NULL OR pi.import_date <= :endDate)

    GROUP BY
        DATE_FORMAT(pi.import_date, '%Y-%m'),
        p.id, p.name, p.used_status,
        c.id, c.name,
        b.id, b.name,
        ct.id, ct.name

    ORDER BY month DESC, p.name
    """,

            countQuery = """
    SELECT COUNT(*) FROM (
        SELECT 1
        FROM product_imports pi
        JOIN products p ON p.id = pi.product_id
        JOIN categories c ON c.id = p.category_id
        JOIN brands b ON b.id = c.brand_id
        JOIN category_types ct ON ct.id = c.category_type_id

        WHERE pi.deleted_at IS NULL
          AND (:startDate IS NULL OR pi.import_date >= :startDate)
          AND (:endDate IS NULL OR pi.import_date <= :endDate)

        GROUP BY
            DATE_FORMAT(pi.import_date, '%Y-%m'),
            p.id,
            c.id,
            b.id,
            ct.id
    ) AS count_table
    """,

            nativeQuery = true
    )
    Page<ExpenseMonthlyReport> getExpenseMonthlyReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
