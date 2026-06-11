package com.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SalesDataPointDto {
    // ISO date string or grouped label (e.g. 2026-06-01, 2026-W23, 2026-06)
    private String period;
    private BigDecimal revenue;
}