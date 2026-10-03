package com.franchise.project.domain.product.spi;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {
    Mono<Product> createProduct(Product product);
    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Product> updateProduct(Product product);
    Flux<Product> findProductByBranchId(Long branchId);
}
