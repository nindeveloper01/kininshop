package com.phoneshop.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long phoneId;
    private String phoneModel;
    private String brandName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
