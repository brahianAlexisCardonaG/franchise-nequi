package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.CreateProductServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateProductUseCase implements CreateProductServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<ProductBranch> createProduct(Product product) {
        return validationCondition.validate(product, Product::hasNonNegativeStock, TechnicalMessage.PRODUCT_STOCK_INVALID)
                .flatMap(validProduct -> branchPersistencePort.findById(validProduct.getBranchId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.BRANCH_NOT_EXISTS)))
                .flatMap(branch -> productPersistencePort.existsByNameAndBranchId(product.getName(), branch.getId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                        .then(Mono.defer(() -> productPersistencePort.createProduct(product)))
                        .map(savedProduct -> ProductBranch.builder()
                                .id(savedProduct.getId())
                                .name(savedProduct.getName())
                                .stock(savedProduct.getStock())
                                .branch(branch)
                                .build()));
    }
}
