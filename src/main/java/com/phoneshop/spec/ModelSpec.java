package com.phoneshop.spec;


import com.phoneshop.entity.Brand;
import com.phoneshop.entity.Model;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("serial")
@RequiredArgsConstructor
public class ModelSpec implements Specification<Model> {
    private final ModelFilter modelFilter;

    @Override
    public Predicate toPredicate(Root<Model> model, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> list = new ArrayList<>();

        // Use LEFT join to ensure models without brands aren't filtered out by default
        Join<Model, Brand> brand = model.join("brand", JoinType.LEFT);

        if (modelFilter.getModelId() != null) {
            list.add(cb.equal(model.get("id"), modelFilter.getModelId()));
        }

        if (modelFilter.getModelName() != null) {
            // Wrap both sides in UPPER for a true case-insensitive search
            list.add(cb.like(cb.upper(model.get("name")),
                    "%" + modelFilter.getModelName().toUpperCase() + "%"));
        }

        if (modelFilter.getBrandId() != null) {
            list.add(cb.equal(brand.get("id"), modelFilter.getBrandId()));
        }

        if (modelFilter.getBrandName() != null) {
            list.add(cb.like(cb.upper(brand.get("name")),
                    "%" + modelFilter.getBrandName().toUpperCase() + "%"));
        }

        return cb.and(list.toArray(new Predicate[0]));
    }
}