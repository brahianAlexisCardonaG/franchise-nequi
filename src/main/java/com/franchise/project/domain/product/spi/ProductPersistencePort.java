package com.franchise.project.domain.product.spi;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductPersistencePort {
    Mono<Product> createProduct(Mono<Product> product);
    Mono<Boolean> findByName(String name);
    Mono<Product> findById(Long id);
    Mono<Void> deleteRelateProductBranch(Mono<Product> product);
    Mono<Product> updateProductStock(Mono<Product> product);
}
