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

import java.math.BigInteger;
import java.util.List;

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
                                        productPersistencePort.createProduct(Mono.just(prod))
                                                .flatMap(prodSave ->
                                                        branchPersistencePort.findById(prod.getBranchId())
                                                                .flatMap(branExist -> {
                                                                    if (branExist == null) {
                                                                        return Mono.error(new BusinessException(TechnicalMessage
                                                                                .BRANCH_NOT_EXISTS));
                                                                    }
                                                                    return Mono.just(branExist);
                                                                })
                                                                .flatMap(bran -> {
                                                                    ProductBranch productBranch = new ProductBranch();
                                                                    productBranch.setId(prodSave.getId());
                                                                    productBranch.setName(prodSave.getName());
                                                                    productBranch.setStock(prodSave.getStock());

                                                                    productBranch.setBranch(bran);
                                                                    return Mono.just(productBranch);
                                                                }))
                                )
                );
    }

    @Override
    public Mono<Void> deleteProductBranch(Long productId) {
        return productPersistencePort.findById(productId)
                .flatMap(prodExist -> {
                    if (prodExist == null) {
                        return Mono.error(new BusinessException(TechnicalMessage
                                .BRANCH_NOT_EXISTS));
                    }
                    return Mono.just(prodExist);
                }).flatMap(product -> {
                    product.setBranchId(null);
                    return productPersistencePort.deleteRelateProductBranch(Mono.just(product));
                }).then();
    }
}
