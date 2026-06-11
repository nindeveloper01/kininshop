package com.phoneshop.spec;

import com.phoneshop.entity.Sale;
import com.phoneshop.entity.SaleDetail;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class SaleDetailSpec implements Specification<SaleDetail> {
    private SaleDetailFilter detailFilter;


    @Override
    public Predicate toPredicate(Root<SaleDetail> saleDetail, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<SaleDetail, Sale> sale = saleDetail.join("sale");
        if(Objects.nonNull(detailFilter.getStartDate())) {
            cb.greaterThanOrEqualTo(sale.get("saleDate"), detailFilter.getStartDate());
        }
        if(Objects.nonNull(detailFilter.getEndDate())) {
            cb.lessThanOrEqualTo(sale.get("soldDate"), detailFilter.getEndDate());
        }
        return cb.and(predicates.toArray(Predicate[]::new));
    }

}