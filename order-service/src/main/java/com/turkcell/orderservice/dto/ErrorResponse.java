package com.turkcell.orderservice.dto;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<String> details
) {}
