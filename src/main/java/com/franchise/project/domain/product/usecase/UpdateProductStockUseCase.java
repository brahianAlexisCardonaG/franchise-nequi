package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.UpdateProductStockServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase implements UpdateProductStockServicePort {

    private final ProductPersistencePort productPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Product> updateProductStock(Product product) {
        return validationCondition.validate(product, Product::hasNonNegativeStock, TechnicalMessage.PRODUCT_STOCK_INVALID)
                .flatMap(validProduct -> productPersistencePort.findById(validProduct.getId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing -> productPersistencePort.updateProduct(
                        existing.toBuilder().stock(product.getStock()).build()));
    }
}
