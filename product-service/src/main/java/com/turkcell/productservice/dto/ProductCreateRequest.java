package com.turkcell.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @NotBlank @Size(min = 1, max = 200) String name,
        @DecimalMin(value = "0", inclusive = false) double price,
        @Min(0) int stock,
        @NotBlank @Size(min = 1, max = 64) String sku
) {}
