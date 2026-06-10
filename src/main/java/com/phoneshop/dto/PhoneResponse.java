package com.phoneshop.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PhoneResponse {
    private Long id;
    private String model;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Long brandId;
    private String brandName;
    private String brandCountry;
}
