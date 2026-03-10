package com.turkcell.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public record OrderCreateRequest(
        @NotNull @PositiveOrZero double totalPrice,
        @NotNull OffsetDateTime orderDate,
        @NotNull OrderStatus status
) {}
