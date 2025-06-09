package com.franchise.project.domain.product.api;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface ProductServicePort {
    Mono<ProductBranch> createProduct(Mono<Product> product);
    Mono<Void> deleteProductBranch(Long productId);
}
