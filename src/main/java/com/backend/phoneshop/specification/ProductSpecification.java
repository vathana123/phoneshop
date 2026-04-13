package com.backend.phoneshop.specification;

import com.backend.phoneshop.dto.RelationshipFilter;
import com.backend.phoneshop.entity.Category;
import com.backend.phoneshop.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecification implements Specification<Product> {

    private Map<String, Object> filters;

    @Override
    public Predicate toPredicate(Root<Product> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        if (filters != null) {

            Predicate searchPredicate = SearchFilterSpecification.<Product>builder()
                    .filters(filters)
                    .fields(List.of("name"))
                    .build()
                    .toPredicate(root, query, cb);

            predicates.add(searchPredicate);

            Predicate equalPredicate = EqualFilterSpecification.<Product>builder()
                    .filters(filters)
                    .fields(List.of("color", "usedStatus", "spec"))
                    .build()
                    .toPredicate(root, query, cb);

            predicates.add(equalPredicate);

            Predicate relationshipPredicate = RelationshipFilterSpecification.<Product>builder()
                    .filters(filters)
                    .fields(Map.of(
                                    "categoryId", RelationshipFilter.builder().entityKey("category").build()
                            )
                    )
                    .build()
                    .toPredicate(root, query, cb);

            predicates.add(relationshipPredicate);
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }
}
