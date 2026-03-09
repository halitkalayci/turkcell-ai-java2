package com.turkcell.productservice;

import com.turkcell.productservice.dto.ProductPage;
import com.turkcell.productservice.dto.StockUpdateRequest;
import com.turkcell.productservice.dto.StockUpdateResponse;
import com.turkcell.productservice.dto.v2.ProductCreateRequestV2;
import com.turkcell.productservice.dto.v2.ProductResponseV2;
import com.turkcell.productservice.dto.v2.ProductUpdateRequestV2;
import com.turkcell.productservice.service.ProductServiceV2;
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
@RequestMapping("/api/v2/products")
public class ProductControllerV2 {

    private final ProductServiceV2 productService;

    public ProductControllerV2(ProductServiceV2 productService) {
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
    public ResponseEntity<ProductResponseV2> createProduct(
            @RequestBody @Valid ProductCreateRequestV2 request,
            UriComponentsBuilder uriBuilder
    ) {
        ProductResponseV2 response = productService.create(request);
        return ResponseEntity
                .created(uriBuilder.path("/api/v2/products/{id}").buildAndExpand(response.id()).toUri())
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseV2> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseV2> replaceProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductUpdateRequestV2 request
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
