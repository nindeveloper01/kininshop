package com.phoneshop.dto;

import com.phoneshop.entity.Order;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private Order.Status status;
    private LocalDateTime createdAt;
}
