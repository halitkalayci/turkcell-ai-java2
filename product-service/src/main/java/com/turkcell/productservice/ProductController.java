package com.turkcell.productservice;

import com.turkcell.productservice.dto.*;
import com.turkcell.productservice.service.ProductServiceV1;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductServiceV1 productService;

    public ProductController(ProductServiceV1 productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ProductPage> listProducts(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size,
            @RequestParam(required = false) String q
    ) {
        return ResponseEntity.ok(productService.list(page, size, q));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        ProductResponse response = productService.create(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/v1/products/{id}").buildAndExpand(response.id()).toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> replaceProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(productService.replace(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-stock")
    public ResponseEntity<StockUpdateResponse> updateStock(@RequestBody @Valid StockUpdateRequest request) {
        return ResponseEntity.ok(productService.updateStock(request));
    }
}
