package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.UpdateProductNameServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase implements UpdateProductNameServicePort {

    private final ProductPersistencePort productPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Product> updateProductName(Product product) {
        return productPersistencePort.findById(product.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing -> productPersistencePort.existsByNameAndBranchId(product.getName(), existing.getBranchId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                        .then(Mono.defer(() -> productPersistencePort.updateProduct(
                                existing.toBuilder().name(product.getName()).build()))));
    }
}
