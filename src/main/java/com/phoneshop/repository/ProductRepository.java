package com.phoneshop.repository;

import com.phoneshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByModelIdAndColorId(Long modelId, Long colorId);
}
