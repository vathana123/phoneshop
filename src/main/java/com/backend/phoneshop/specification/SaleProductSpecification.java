package com.backend.phoneshop.specification;

import com.backend.phoneshop.entity.Product;
import com.backend.phoneshop.entity.SaleDetail;
import com.backend.phoneshop.entity.SaleProduct;
import lombok.*;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleProductSpecification implements Specification<SaleProduct> {

    private Map<String, Object> filters;

    @Override
    public Predicate toPredicate(Root<SaleProduct> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        if (filters != null) {

            // ✅ 1. Date Range Filter (soldAt)
            if (filters.containsKey("startDate") && filters.get("startDate") != null) {
                LocalDateTime start = (LocalDateTime) filters.get("startDate");

                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("soldAt"), start)
                );
            }

            if (filters.containsKey("endDate") && filters.get("endDate") != null) {
                LocalDateTime end = (LocalDateTime) filters.get("endDate");

                predicates.add(
                        cb.lessThanOrEqualTo(root.get("soldAt"), end)
                );
            }

            // ✅ 2. Filter by productId (JOIN saleDetails → product)
            if (filters.containsKey("productId")) {
                Long productId = Long.valueOf(filters.get("productId").toString());

                Join<SaleProduct, SaleDetail> saleDetailJoin =
                        root.join("saleDetails", JoinType.INNER);

                Join<SaleDetail, Product> productJoin =
                        saleDetailJoin.join("product", JoinType.INNER);

                predicates.add(cb.equal(productJoin.get("id"), productId));

                // ⚠️ Prevent duplicate SaleProduct when multiple SaleDetails exist
                query.distinct(true);
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}