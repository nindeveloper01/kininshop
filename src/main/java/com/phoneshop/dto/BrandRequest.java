package com.phoneshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String country;
}
