package com.franchise.project.domain.product.api;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import reactor.core.publisher.Mono;

public interface ProductServicePort {
    Mono<ProductBranch> createProduct(Product product);
    Mono<Void> deleteProductBranch(Long productId);
    Mono<Product> updateStock(Product product);
    Mono<Product> updateName(Product product);
}
