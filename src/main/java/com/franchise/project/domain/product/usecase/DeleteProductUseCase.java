package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.DeleteProductServicePort;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase implements DeleteProductServicePort {

    private final ProductPersistencePort productPersistencePort;

    @Override
    public Mono<Void> deleteProduct(Long productId) {
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(product -> productPersistencePort.deleteById(product.getId()));
    }
}
