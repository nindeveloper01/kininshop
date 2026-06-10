package com.phoneshop.repository;

import com.phoneshop.entity.Phone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    // Find phones where brand name contains the given string (case-insensitive)
    Page<Phone> findByBrandNameContainingIgnoreCase(String brandName, Pageable pageable);

    // Find phones where model contains the given string (case-insensitive)
    Page<Phone> findByModelContainingIgnoreCase(String model, Pageable pageable);

    // Combined search across model and brand name
    @Query("""
        SELECT p FROM Phone p
        WHERE LOWER(p.model) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Phone> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Find only in-stock phones
    Page<Phone> findByStockGreaterThan(int stock, Pageable pageable);
}
