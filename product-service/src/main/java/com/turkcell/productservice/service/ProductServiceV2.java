package com.turkcell.productservice.service;

import com.turkcell.productservice.dto.ProductPage;
import com.turkcell.productservice.dto.StockUpdateRequest;
import com.turkcell.productservice.dto.StockUpdateResponse;
import com.turkcell.productservice.dto.v2.ProductCreateRequestV2;
import com.turkcell.productservice.dto.v2.ProductResponseV2;
import com.turkcell.productservice.dto.v2.ProductUpdateRequestV2;
import com.turkcell.productservice.entity.Product;
import com.turkcell.productservice.exception.ProductNotFoundException;
import com.turkcell.productservice.exception.SkuAlreadyExistsException;
import com.turkcell.productservice.repository.ProductRepository;
import com.turkcell.productservice.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProductServiceV2 {

    private final ProductRepository productRepository;

    public ProductServiceV2(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductPage list(int page, int size, String q) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> result = (q != null && !q.isBlank())
                ? productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(q, q, pageable)
                : productRepository.findAll(pageable);

        return new ProductPage(
                result.getContent().stream().map(this::toV1Response).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public ProductResponseV2 create(ProductCreateRequestV2 request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new SkuAlreadyExistsException(request.sku());
        }
        Product product = new Product(request.name(), request.description(), request.price(), request.stock(), request.sku());
        return toResponse(productRepository.save(product));
    }

    public ProductResponseV2 getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public ProductResponseV2 replace(UUID id, ProductUpdateRequestV2 request) {
        Product product = findOrThrow(id);
        if (!product.getSku().equals(request.sku()) && productRepository.existsBySku(request.sku())) {
            throw new SkuAlreadyExistsException(request.sku());
        }
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setSku(request.sku());
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public StockUpdateResponse updateStock(StockUpdateRequest request) {
        Product product = findOrThrow(request.id());
        product.setStock(request.stock());
        return new StockUpdateResponse(product.getId(), product.getName(), productRepository.save(product).getStock());
    }

    private Product findOrThrow(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponseV2 toResponse(Product p) {
        return new ProductResponseV2(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getSku());
    }

    private ProductResponse toV1Response(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getSku());
    }
}
