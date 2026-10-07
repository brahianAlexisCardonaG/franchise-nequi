package com.franchise.project.usecase.deleteproduct;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase {

    private final ProductRepository productRepository;

    public Mono<Void> deleteProduct(Long productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(product -> productRepository.deleteById(product.getId()));
    }
}
