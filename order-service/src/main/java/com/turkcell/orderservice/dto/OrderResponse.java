package com.turkcell.orderservice.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        double totalPrice,
        OffsetDateTime orderDate,
        OrderStatus status
) {}
