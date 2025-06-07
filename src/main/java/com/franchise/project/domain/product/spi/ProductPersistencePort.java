package com.franchise.project.domain.product.spi;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Mono<Product> createProduct(Mono<Product> product);
    Mono<Boolean> findByName(String name);
}
