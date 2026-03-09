package com.turkcell.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockUpdateRequest(
        @NotNull UUID id,
        @Min(0) int stock
) {}
