package com.turkcell.productservice;

import com.turkcell.productservice.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<ProductPage> listProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size,
            @RequestParam(required = false) String q
    ) {
        // TODO: implement service layer
        return ResponseEntity.ok(new ProductPage(List.of(), page, size, 0, 0));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        // TODO: implement service layer
        UUID id = UUID.randomUUID();
        ProductResponse response = new ProductResponse(id, request.name(), request.price(), request.stock(), request.sku());
        return ResponseEntity
                .created(uriBuilder.path("/api/v1/products/{id}").buildAndExpand(id).toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        // TODO: implement service layer
        return ResponseEntity.ok(new ProductResponse(id, "placeholder", 0.0, 0, "PLACEHOLDER"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> replaceProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductUpdateRequest request
    ) {
        // TODO: implement service layer
        return ResponseEntity.ok(new ProductResponse(id, request.name(), request.price(), request.stock(), request.sku()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        // TODO: implement service layer
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-stock")
    public ResponseEntity<StockUpdateResponse> updateStock(@RequestBody @Valid StockUpdateRequest request) {
        // TODO: implement service layer
        return ResponseEntity.ok(new StockUpdateResponse(request.id(), "placeholder", request.stock()));
    }
}
