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
        return productPersistencePort.findByName(product.getName())
                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .then(Mono.defer(() ->
                        branchPersistencePort.findById(product.getBranchId())
                                .flatMap(branch -> validationCondition.validationExist(branch == null, TechnicalMessage.BRANCH_NOT_EXISTS)
                                        .thenReturn(branch))
                                .flatMap(branch ->
                                        productPersistencePort.createProduct(product)
                                                .map(savedProduct -> new ProductBranch(
                                                        savedProduct.getId(),
                                                        savedProduct.getName(),
                                                        savedProduct.getStock(),
                                                        branch
                                                ))
                                )
                ));
    }

    @Override
    public Mono<Void> deleteProductBranch(Long productId) {
        return productPersistencePort.findById(productId)
                .flatMap(product -> validationCondition.validationExist(product == null, TechnicalMessage.PRODUCT_NOT_EXISTS)
                        .then(Mono.defer(() -> {
                            Product productUpdate = new Product(
                                    product.getId(),
                                    product.getName(),
                                    product.getStock(),
                                    null
                            );
                            return productPersistencePort.deleteRelateProductBranch(productUpdate);
                        }))
                ).then();
    }

    @Override
    public Mono<Product> updateStock(Product product) {
        return productPersistencePort.findById(product.getId())
                .flatMap(existing ->
                        validationCondition.validationExist(existing == null, TechnicalMessage.PRODUCT_NOT_EXISTS)
                                .thenReturn(existing)
                )
                .flatMap(existing ->
                        Mono.defer(() -> {
                            Product updated = new Product(
                                    existing.getId(),
                                    existing.getName(),
                                    product.getStock(), // nuevo stock
                                    existing.getBranchId()
                            );
                            return productPersistencePort.updateProduct(updated);
                        })
                );
    }

    @Override
    public Mono<Product> updateName(Product product) {
        return productPersistencePort.findById(product.getId())
                .flatMap(existing ->
                        validationCondition.validationExist(existing == null, TechnicalMessage.PRODUCT_NOT_EXISTS)
                                .thenReturn(existing)
                )
                .flatMap(existing ->
                        productPersistencePort.findByName(product.getName())
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
}
