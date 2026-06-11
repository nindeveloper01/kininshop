
package com.phoneshop.service.impl;

import com.phoneshop.dto.DashboardSummaryDto;
import com.phoneshop.dto.RecentOrderDto;
import com.phoneshop.dto.SalesDataPointDto;
import com.phoneshop.dto.TopProductDto;
import com.phoneshop.service.DashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private EntityManager em;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @Override
    public DashboardSummaryDto getSummary() {
      return null;
    }

    @Override
    public List<SalesDataPointDto> getSalesAnalytics(String period) {
       return null;
    }

    @Override
    public List<TopProductDto> getTopProducts(int limit) {
        return null;
    }

    @Override
    public List<RecentOrderDto> getRecentOrders(int limit) {

        return null;
    }
}