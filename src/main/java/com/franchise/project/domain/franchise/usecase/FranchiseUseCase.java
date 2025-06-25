package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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
                .flatMap(franchise -> validationCondition.validationExist(franchise == null, TechnicalMessage.FRANCHISE_NOT_EXISTS)
                        .thenReturn(franchise))
                .flatMap(franchise ->
                        branchPersistencePort.findBranchesByFranchiseId(franchiseId)
                                .flatMapMany(Flux::fromIterable)
                                .flatMap(branch ->
                                        productPersistencePort.findProductByBranchId(branch.getId())
                                                .flatMapMany(Flux::fromIterable)
                                                .reduce((p1, p2) ->
                                                        p1.getStock().compareTo(p2.getStock()) >= 0 ? p1 : p2
                                                )
                                                .flatMap(maxProduct ->
                                                        Mono.just(new BranchProduct(
                                                                branch.getId(),
                                                                branch.getName(),
                                                                maxProduct
                                                        ))
                                                )
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
                .flatMap(existing ->
                        validationCondition.validationExist(existing == null, TechnicalMessage.FRANCHISE_NOT_EXISTS)
                                .thenReturn(existing)
                )
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