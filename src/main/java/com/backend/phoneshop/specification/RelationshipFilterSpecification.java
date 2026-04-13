package com.backend.phoneshop.specification;

import com.backend.phoneshop.dto.RelationshipFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RelationshipFilterSpecification<T> implements Specification<T> {

    private Map<String, Object> filters;
    private Map<String, RelationshipFilter> fields;

    @Override
    public Predicate toPredicate(Root<T> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        if (filters == null || fields == null || fields.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> predicates =  new ArrayList<>();
        for (String fieldKey : fields.keySet() ) {
            if (filters.containsKey(fieldKey)) {
                Long fieldId = Long.valueOf(filters.get(fieldKey).toString());

                predicates.add(
                        cb.equal(root.get(fields.get(fieldKey).entityKey()).get(fields.get(fieldKey).idKey()), fieldId)
                );
            }
        }

        if (predicates.isEmpty()) {
            return cb.conjunction();
        }

        return cb.or(predicates.toArray(Predicate[]::new));
    }
}
