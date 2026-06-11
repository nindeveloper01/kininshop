package com.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class RecentOrderDto {
    private String orderId;
    private OffsetDateTime createdAt;
    private BigDecimal totalAmount;
    private String status;
}