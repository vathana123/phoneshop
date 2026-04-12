package com.backend.phoneshop.specification;

import com.backend.phoneshop.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpecification implements Specification<Category> {

    private Map<String, Object> filters;

    @Override
    public Predicate toPredicate(Root<Category> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        if (filters != null) {

            // 🔍 Filter by name (LIKE)
            if (filters.get("name") != null) {
                String name = filters.get("name").toString().toLowerCase();

                if (!name.isBlank()) {
                    predicates.add(
                            cb.like(
                                    cb.lower(root.get("name")),
                                    "%" + name + "%"
                            )
                    );
                }
            }

            // 🔗 Filter by brandId
            if (filters.get("brandId") != null) {
                Long brandId = Long.valueOf(filters.get("brandId").toString());

                predicates.add(
                        cb.equal(root.get("brand").get("id"), brandId)
                );
            }

            // 🔗 Filter by categoryTypeId
            if (filters.get("categoryTypeId") != null) {
                Long categoryTypeId = Long.valueOf(filters.get("categoryTypeId").toString());

                predicates.add(
                        cb.equal(root.get("categoryType").get("id"), categoryTypeId)
                );
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
