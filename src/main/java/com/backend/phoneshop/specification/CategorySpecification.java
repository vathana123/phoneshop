package com.backend.phoneshop.specification;

import com.backend.phoneshop.dto.request.RelationshipFilter;
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

            Predicate searchPredicate = SearchFilterSpecification.<Category>builder()
                    .filters(filters)
                    .fields(List.of("name"))
                    .build()
                    .toPredicate(root, query, cb);

            predicates.add(searchPredicate);

            Predicate relationshipPredicate = RelationshipFilterSpecification.<Category>builder()
                    .filters(filters)
                    .fields(Map.of(
                            "brandId", RelationshipFilter.builder().entityKey("brand").build(),
                            "categoryTypeId",  RelationshipFilter.builder().entityKey("categoryType").build()
                            )
                    )
                    .build()
                    .toPredicate(root, query, cb);

            predicates.add(relationshipPredicate);
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }
}
