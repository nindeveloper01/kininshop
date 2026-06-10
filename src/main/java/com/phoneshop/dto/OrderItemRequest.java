package com.phoneshop.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotNull
    private Long phoneId;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
