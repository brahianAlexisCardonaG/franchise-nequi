package com.franchise.project.usecase.updateproductname;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private final ProductRepository productRepository;
    private final ValidationCondition validationCondition;

    public Mono<Product> updateProductName(Product product) {
        return productRepository.findById(product.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing -> productRepository.existsByNameAndBranchId(product.getName(), existing.getBranchId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                        .then(Mono.defer(() -> productRepository.updateProduct(
                                existing.toBuilder().name(product.getName()).build()))));
    }
}
