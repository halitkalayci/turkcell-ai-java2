package com.turkcell.productservice.dto.v2;

import java.util.UUID;

public record ProductResponseV2(
        UUID id,
        String name,
        String description,
        double price,
        int stock,
        String sku
) {}
