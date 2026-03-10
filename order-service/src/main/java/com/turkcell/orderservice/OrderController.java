package com.turkcell.orderservice;

import com.turkcell.orderservice.dto.OrderCreateRequest;
import com.turkcell.orderservice.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        return ResponseEntity
                .created(uriBuilder.path("/api/v1/orders/{id}").buildAndExpand("").toUri())
                .body(null);
    }
}
