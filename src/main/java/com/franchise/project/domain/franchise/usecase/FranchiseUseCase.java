package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.function.BinaryOperator;

@RequiredArgsConstructor
public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;
    private final BranchPersistencePort branchPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchisePersistencePort.existsByName(franchise.getName())
                .flatMap(exists -> validationCondition.validationExist(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .then(Mono.defer(() -> franchisePersistencePort.createFranchise(franchise)));
    }

    @Override
    public Mono<FranchiseBranchProductList> getFranchiseBranchProduct(Long franchiseId) {
        return franchisePersistencePort.findById(franchiseId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise -> branchPersistencePort.findBranchesByFranchiseId(franchise.getId())
                        .flatMap(this::findLargestStockProduct)
                        .collectList()
                        .map(branches -> FranchiseBranchProductList.builder()
                                .id(franchise.getId())
                                .name(franchise.getName())
                                .branches(branches)
                                .build()));
    }

    @Override
    public Mono<Franchise> updateName(Franchise franchise) {
        return franchisePersistencePort.findById(franchise.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(existing -> franchisePersistencePort.existsByName(franchise.getName())
                        .flatMap(exists -> validationCondition.validationExist(exists, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        .then(Mono.defer(() -> franchisePersistencePort.updateFranchise(
                                existing.toBuilder().name(franchise.getName()).build()))));
    }

    private Mono<BranchProduct> findLargestStockProduct(Branch branch) {
        return productPersistencePort.findProductByBranchId(branch.getId())
                .reduce(BinaryOperator.maxBy(Comparator.comparing(Product::getStock)))
                .map(product -> BranchProduct.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .product(product)
                        .build())
                .defaultIfEmpty(BranchProduct.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .build());
    }
}
