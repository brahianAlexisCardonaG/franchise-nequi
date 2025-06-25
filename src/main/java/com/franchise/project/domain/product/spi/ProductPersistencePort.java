package com.franchise.project.domain.product.spi;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductPersistencePort {
    Mono<Product> createProduct(Product product);
    Mono<Boolean> findByName(String name);
    Mono<Product> findById(Long id);
    Mono<Void> deleteRelateProductBranch(Product product);
    Mono<Product> updateProduct(Product product);
    Mono<List<Product>> findProductByBranchId(Long branchId);
}
