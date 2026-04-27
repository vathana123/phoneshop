package com.backend.phoneshop.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DateRangFilterSpecification<T> implements Specification<T> {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String field;

    @Override
    public Predicate toPredicate(Root<T> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        if (field == null || field.isBlank() || (startDate == null && endDate == null)) {
            return cb.conjunction();
        }

        List<Predicate> predicates =  new ArrayList<>();

        // ✅ 1. Date Range Filter (soldAt)
        if (startDate != null) {

            predicates.add(
                    cb.greaterThanOrEqualTo(root.get(field), startDate)
            );
        }

        if (endDate != null) {

            predicates.add(
                    cb.lessThanOrEqualTo(root.get(field), endDate)
            );
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }
}
