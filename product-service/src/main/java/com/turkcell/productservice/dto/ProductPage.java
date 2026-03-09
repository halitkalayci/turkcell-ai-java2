package com.turkcell.productservice.dto;

import java.util.List;

public record ProductPage(
        List<ProductResponse> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {}
