package com.turkcell.productservice.dto;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        double price,
        int stock,
        String sku
) {}
