package com.backend.phoneshop.repository;

import com.backend.phoneshop.dto.data.SaleProductMonthlyReport;
import com.backend.phoneshop.dto.data.SaleProductReport;
import com.backend.phoneshop.entity.SaleDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SaleProductReportRepository
        extends JpaRepository<SaleDetail, Long>,
        JpaSpecificationExecutor<SaleDetail> {

    // 🔹 Detail Report
    @Query(
            value = """
        SELECT
            sp.id AS saleProductId,
            sd.id AS saleDetailId,
            p.id AS productId,
            p.name AS productName,
            p.used_status AS productUsedStatus,
            c.id AS categoryId,
            c.name AS categoryName,
            b.id AS brandId,
            b.name AS brandName,
            ct.id AS categoryTypeId,
            ct.name AS categoryTypeName,
            sp.sold_at AS soldAt,
            sd.quantity,
            sd.sale_amount AS saleAmount,
            sd.discount
        FROM sale_details sd
        JOIN sale_products sp ON sp.id = sd.sale_product_id
        JOIN products p ON p.id = sd.product_id
        JOIN categories c ON c.id = p.category_id
        JOIN brands b ON b.id = c.brand_id
        JOIN category_types ct ON ct.id = c.category_type_id
        WHERE sd.canceled_at IS NULL
          AND (:startDate IS NULL OR sp.sold_at >= :startDate)
          AND (:endDate IS NULL OR sp.sold_at <= :endDate)
        """,

            countQuery = """
        SELECT COUNT(*)
        FROM sale_details sd
        JOIN sale_products sp ON sp.id = sd.sale_product_id
        WHERE sd.canceled_at IS NULL
          AND (:startDate IS NULL OR sp.sold_at >= :startDate)
          AND (:endDate IS NULL OR sp.sold_at <= :endDate)
        """,

            nativeQuery = true
    )
    Page<SaleProductReport> getSaleProductReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );


    // 🔹 Monthly Summary Report (MySQL)
    @Query(
            value = """
        SELECT
            DATE_FORMAT(sp.sold_at, '%Y-%m') AS month,
            p.id AS productId,
            p.name AS productName,
            p.used_status AS productUsedStatus,
            c.id AS categoryId,
            c.name AS categoryName,
            b.id AS brandId,
            b.name AS brandName,
            ct.id AS categoryTypeId,
            ct.name AS categoryTypeName,
            SUM(sd.quantity) AS soleProductCount,
            CAST(SUM(sd.sale_amount * sd.quantity) AS DECIMAL(18,2)) AS totalPaymentAmount,
            CAST(SUM(DISTINCT sp.paid_amount) AS DECIMAL(18,2)) AS totalPaidAmount
        FROM sale_details sd
        JOIN sale_products sp ON sp.id = sd.sale_product_id
        JOIN products p ON p.id = sd.product_id
        JOIN categories c ON c.id = p.category_id
        JOIN brands b ON b.id = c.brand_id
        JOIN category_types ct ON ct.id = c.category_type_id

        WHERE sd.canceled_at IS NULL
          AND (:startDate IS NULL OR sp.sold_at >= :startDate)
          AND (:endDate IS NULL OR sp.sold_at <= :endDate)
        GROUP BY
            DATE_FORMAT(sp.sold_at, '%Y-%m'),
            p.id, p.name, p.used_status,
            c.id, c.name,
            b.id, b.name,
            ct.id, ct.name
        ORDER BY month DESC, p.name ASC
        """,

            countQuery = """
        SELECT COUNT(*) FROM (
            SELECT 1
            FROM sale_details sd
            JOIN sale_products sp ON sp.id = sd.sale_product_id
            JOIN products p ON p.id = sd.product_id
            JOIN categories c ON c.id = p.category_id
            JOIN brands b ON b.id = c.brand_id
            JOIN category_types ct ON ct.id = c.category_type_id

            WHERE sd.canceled_at IS NULL
              AND (:startDate IS NULL OR sp.sold_at >= :startDate)
              AND (:endDate IS NULL OR sp.sold_at <= :endDate)

            GROUP BY
                DATE_FORMAT(sp.sold_at, '%Y-%m'),
                p.id,
                c.id,
                b.id,
                ct.id
        ) AS count_table
        """,

            nativeQuery = true
    )
    Page<SaleProductMonthlyReport> getMonthlyReport(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}