package com.franchise.project.usecase.createproduct;

import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.ProductBranch;
import com.franchise.project.model.product.gateways.ProductRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateProductUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ValidationCondition validationCondition;

    public Mono<ProductBranch> createProduct(Product product) {
        return validationCondition.validate(product, Product::hasNonNegativeStock, TechnicalMessage.PRODUCT_STOCK_INVALID)
                .flatMap(validProduct -> branchRepository.findById(validProduct.getBranchId()))
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.BRANCH_NOT_EXISTS)))
                .flatMap(branch -> productRepository.existsByNameAndBranchId(product.getName(), branch.getId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                        .then(Mono.defer(() -> productRepository.createProduct(product)))
                        .map(savedProduct -> ProductBranch.builder()
                                .id(savedProduct.getId())
                                .name(savedProduct.getName())
                                .stock(savedProduct.getStock())
                                .branch(branch)
                                .build()));
    }
}
