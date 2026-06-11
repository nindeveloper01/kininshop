package com.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardSummaryDto {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalUsers;
    private long lowStockProducts;
}