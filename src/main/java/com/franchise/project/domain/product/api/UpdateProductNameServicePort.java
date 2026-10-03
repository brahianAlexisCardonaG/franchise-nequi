package com.franchise.project.domain.product.api;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductNameServicePort {
    Mono<Product> updateProductName(Product product);
}
