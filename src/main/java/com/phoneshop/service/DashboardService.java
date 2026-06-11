package com.phoneshop.service;

import com.phoneshop.dto.*;
import java.util.List;

public interface DashboardService {
    DashboardSummaryDto getSummary();
    List<SalesDataPointDto> getSalesAnalytics(String period);
    List<TopProductDto> getTopProducts(int limit);
    List<RecentOrderDto> getRecentOrders(int limit);
}
