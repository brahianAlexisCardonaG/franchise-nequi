package com.franchise.project.domain.product.api;

import reactor.core.publisher.Mono;

public interface DeleteProductServicePort {
    Mono<Void> deleteProduct(Long productId);
}
