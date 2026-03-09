package com.turkcell.productservice.dto.v2;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductUpdateRequestV2(
        @NotBlank @Size(min = 1, max = 200) String name,
        @NotBlank @Size(min = 3, max = 255) String description,
        @DecimalMin(value = "0", inclusive = false) double price,
        @Min(1) int stock,
        @NotBlank @Size(min = 1, max = 64) String sku
) {}
