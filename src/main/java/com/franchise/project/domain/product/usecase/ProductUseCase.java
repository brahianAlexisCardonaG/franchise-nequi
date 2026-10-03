package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase implements ProductServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<ProductBranch> createProduct(Product product) {
        return validateStock(product)
                .flatMap(validProduct -> branchPersistencePort.findById(validProduct.getBranchId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.BRANCH_NOT_EXISTS)))
                .flatMap(branch ->
                        productPersistencePort.existsByNameAndBranchId(product.getName(), branch.getId())
                                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                                .then(Mono.defer(() -> productPersistencePort.createProduct(product)))
                                .map(savedProduct -> new ProductBranch(
                                        savedProduct.getId(),
                                        savedProduct.getName(),
                                        savedProduct.getStock(),
                                        branch
                                ))
                );
    }

    @Override
    public Mono<Void> deleteProductBranch(Long productId) {
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(product -> productPersistencePort.deleteById(product.getId()));
    }

    @Override
    public Mono<Product> updateStock(Product product) {
        return validateStock(product)
                .flatMap(validProduct -> productPersistencePort.findById(validProduct.getId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing -> productPersistencePort.updateProduct(new Product(
                        existing.getId(),
                        existing.getName(),
                        product.getStock(),
                        existing.getBranchId()
                )));
    }

    @Override
    public Mono<Product> updateName(Product product) {
        return productPersistencePort.findById(product.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)))
                .flatMap(existing ->
                        productPersistencePort.existsByNameAndBranchId(product.getName(), existing.getBranchId())
                                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                                .then(Mono.defer(() -> {
                                    Product updated = new Product(
                                            existing.getId(),
                                            product.getName(),
                                            existing.getStock(),
                                            existing.getBranchId()
                                    );
                                    return productPersistencePort.updateProduct(updated);
                                }))
                );
    }

    private Mono<Product> validateStock(Product product) {
        return Mono.just(product)
                .filter(candidate -> candidate.getStock().signum() >= 0)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.PRODUCT_STOCK_INVALID)));
    }
}
