package com.franchise.project.domain.franchise.usecase;

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
import reactor.core.publisher.Flux;
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
        return franchisePersistencePort.findByName(franchise.getName())
                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .then(Mono.defer(() ->franchisePersistencePort.createFranchise(franchise)));
    }

    @Override
    public Mono<FranchiseBranchProductList> getFranchiseBranchProduct(Long franchiseId) {
        return franchisePersistencePort.findById(franchiseId)
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise ->
                        branchPersistencePort.findBranchesByFranchiseId(franchiseId)
                                .flatMapMany(Flux::fromIterable)
                                .flatMap(branch ->
                                        productPersistencePort.findProductByBranchId(branch.getId())
                                                .flatMapMany(Flux::fromIterable)
                                                .reduce(BinaryOperator.maxBy(Comparator.comparing(Product::getStock)))
                                                .map(maxProduct -> new BranchProduct(
                                                        branch.getId(),
                                                        branch.getName(),
                                                        maxProduct
                                                ))
                                                .switchIfEmpty(
                                                        Mono.just(new BranchProduct(
                                                                branch.getId(),
                                                                branch.getName(),
                                                                null
                                                        ))
                                                )
                                )
                                .collectList()
                                .map(branchList ->
                                    new FranchiseBranchProductList(
                                            franchise.getId(),
                                            franchise.getName(),
                                            branchList)
                                )
                );
    }

    @Override
    public Mono<Franchise> updateName(Franchise franchise) {
        return franchisePersistencePort.findById(franchise.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(existing ->
                        franchisePersistencePort.findByName(franchise.getName())
                                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                                .then(Mono.defer(() -> {
                                    Franchise updated = new Franchise(existing.getId(), franchise.getName());
                                    return franchisePersistencePort.updateFranchise(updated);
                                }))
                );
    }
}