package com.backend.phoneshop.specification;

import com.backend.phoneshop.entities.Brand;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class BrandSpecification implements Specification<Brand> {

    private String search;

    @Override
    public Predicate toPredicate(Root<Brand> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            predicates.add(
                    cb.like(
                            cb.lower(root.get("name")),
                            "%" + search.toLowerCase() + "%"
                    )
            );
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }
}