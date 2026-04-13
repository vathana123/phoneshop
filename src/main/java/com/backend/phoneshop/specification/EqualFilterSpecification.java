package com.backend.phoneshop.specification;

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
public class EqualFilterSpecification<T> implements Specification<T> {

    private Map<String, Object> filters;
    private List<String> fields;

    @Override
    public Predicate toPredicate(Root<T> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        if (filters == null || fields == null || fields.isEmpty()) {
            return cb.conjunction();
        }

        List<Predicate> predicates =  new ArrayList<>();
        for (String field: fields){
            if (filters.containsKey(field)) {
                predicates.add(cb.equal(root.get(field), filters.get(field)));
            }
        }

        return cb.and(predicates.toArray(Predicate[]::new));
    }
}
