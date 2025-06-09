package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase implements ProductServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final ProductPersistencePort productPersistencePort;

    @Override
    public Mono<ProductBranch> createProduct(Mono<Product> product) {
        return product
                .flatMap(prod ->
                        productPersistencePort.findByName(prod.getName())
                                .filter(exist -> !exist)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                        .PRODUCT_ALREADY_EXISTS)))
                                .flatMap(ignore ->
                                        branchPersistencePort.findById(prod.getBranchId())
                                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                                        .BRANCH_NOT_EXISTS))))
                                .flatMap(bran ->
                                        productPersistencePort.createProduct(Mono.just(prod))
                                                .flatMap(branSaved -> {
                                                    ProductBranch productBranch = new ProductBranch();
                                                    productBranch.setId(branSaved.getId());
                                                    productBranch.setName(branSaved.getName());
                                                    productBranch.setStock(branSaved.getStock());

                                                    productBranch.setBranch(bran);
                                                    return Mono.just(productBranch);
                                                }))
                );
    }

    @Override
    public Mono<Void> deleteProductBranch(Long productId) {
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                        .PRODUCT_NOT_EXISTS)))
                .flatMap(product -> {
                    product.setBranchId(null);
                    return productPersistencePort.deleteRelateProductBranch(Mono.just(product));
                }).then();
    }

    @Override
    public Mono<Product> updateStock(Mono<Product> productMono) {
        return productMono
                .flatMap(product -> productPersistencePort.findById(product.getId())
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                .PRODUCT_NOT_EXISTS)))
                        .flatMap(
                                prod -> {
                                    prod.setStock(product.getStock());
                                    return productPersistencePort.updateProduct(Mono.just(prod));
                                }
                        ));
    }

    @Override
    public Mono<Product> updateName(Mono<Product> productMono) {
        return productMono
                .flatMap(product ->
                        productPersistencePort.findById(product.getId())
                                .switchIfEmpty(Mono.error(
                                        new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)
                                ))
                                .flatMap(prodExist ->
                                        productPersistencePort.findByName(product.getName())
                                                .filter(exist -> !exist)
                                                .switchIfEmpty(Mono.error(
                                                        new BusinessException(TechnicalMessage
                                                        .PRODUCT_ALREADY_EXISTS)))
                                                .flatMap(ignore -> {
                                                    prodExist.setName(product.getName());
                                                    return productPersistencePort.updateProduct(Mono.just(prodExist));
                                                }))
                );
    }
}
