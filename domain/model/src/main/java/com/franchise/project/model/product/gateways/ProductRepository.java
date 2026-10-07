package com.franchise.project.model.product.gateways;

import com.franchise.project.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> createProduct(Product product);
    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Product> updateProduct(Product product);
    Flux<Product> findProductByBranchId(Long branchId);
}
