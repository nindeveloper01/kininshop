package com.phoneshop.service;


import com.phoneshop.dto.ExpenseReportDTO;
import com.phoneshop.dto.ProductReportDTO;
import com.phoneshop.projection.ProductSold;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ProductSold> getProductSold(LocalDate startDate, LocalDate endDate);

    List<ProductReportDTO> getProductReport(LocalDate startDate, LocalDate endDate);

    List<ExpenseReportDTO> getExpenseReport(LocalDate startDate, LocalDate endDate);
}
