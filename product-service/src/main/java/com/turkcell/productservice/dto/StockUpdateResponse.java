package com.turkcell.productservice.dto;

import java.util.UUID;

public record StockUpdateResponse(
        UUID id,
        String name,
        int stock
) {}
