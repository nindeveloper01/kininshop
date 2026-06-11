package com.phoneshop.controller;

import com.phoneshop.dto.*;
import com.phoneshop.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Gets overall high-level metrics for top summary cards")
    public ResponseEntity<DashboardSummaryDto> summary() {
        DashboardSummaryDto dto = dashboardService.getSummary();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/sales-analytics")
    @Operation(summary = "Provides data to build line/bar charts for revenue trends")
    public ResponseEntity<List<SalesDataPointDto>> salesAnalytics(
            @RequestParam(name = "period", required = false, defaultValue = "monthly") String period
    ) {
        List<SalesDataPointDto> series = dashboardService.getSalesAnalytics(period);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/top-products")
    @Operation(summary = "Returns the top best-selling phones")
    public ResponseEntity<List<TopProductDto>> topProducts(
            @RequestParam(name = "limit", required = false, defaultValue = "5") @Min(1) int limit
    ) {
        List<TopProductDto> list = dashboardService.getTopProducts(limit);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/recent-orders")
    @Operation(summary = "Quick table of the latest orders with their status")
    public ResponseEntity<List<RecentOrderDto>> recentOrders(
            @RequestParam(name = "limit", required = false, defaultValue = "5") @Min(1) int limit
    ) {
        List<RecentOrderDto> list = dashboardService.getRecentOrders(limit);
        return ResponseEntity.ok(list);
    }

}
