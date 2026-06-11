package com.phoneshop.repository;

import com.phoneshop.entity.ProductImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductImportHistoryRepository  extends JpaRepository<ProductImportHistory, Long>, JpaSpecificationExecutor<ProductImportHistory> {
}