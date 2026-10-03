package com.franchise.project.domain.product.spi;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductPersistencePort {
    Mono<Product> createProduct(Product product);
    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<Product> updateProduct(Product product);
    Mono<List<Product>> findProductByBranchId(Long branchId);
}
