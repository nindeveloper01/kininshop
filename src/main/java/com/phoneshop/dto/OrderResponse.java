package com.phoneshop.dto;

import com.phoneshop.entity.Order;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private UUID userId;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private Order.Status status;
    private LocalDateTime createdAt;
}
