package com.franchise.project.domain.product.api;

import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductStockServicePort {
    Mono<Product> updateProductStock(Product product);
}
