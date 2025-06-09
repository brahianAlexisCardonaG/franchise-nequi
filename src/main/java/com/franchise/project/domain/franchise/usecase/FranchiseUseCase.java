package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class FranchiseUseCase implements FranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;
    private final BranchPersistencePort branchPersistencePort;
    private final ProductPersistencePort productPersistencePort;

    @Override
    public Mono<Franchise> createFranchise(Mono<Franchise> franchise) {
        return franchise
                .flatMap(fran ->
                        franchisePersistencePort.findByName(fran.getName())
                                .filter(exist -> !exist)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                        .FRANCHISE_ALREADY_EXISTS)))
                                .flatMap(ignore ->
                                        franchisePersistencePort.createFranchise(Mono.just(fran)))
                );
    }

    @Override
    public Mono<FranchiseBranchProductList> getFranchiseBranchProduct(Long franchiseId) {
        return franchisePersistencePort.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise -> branchPersistencePort
                        .findBranchesByFranchiseId(franchiseId)
                        .flatMap(branch -> productPersistencePort
                                .findProductByBranchId(branch.getId()).reduce((p1, p2) ->
                                        p1.getStock().compareTo(p2.getStock()) >= 0 ? p1 : p2
                                )
                                .flatMap(maxProduct -> Mono.just(
                                        new BranchProduct(
                                                branch.getId(),
                                                branch.getName(),
                                                maxProduct
                                        )
                                ))
                                .switchIfEmpty(Mono.just(
                                        new BranchProduct(
                                                branch.getId(),
                                                branch.getName(),
                                                null
                                        )
                                ))
                        )
                        .collectList()
                        .map(
                                brachList -> {
                                    FranchiseBranchProductList result = new FranchiseBranchProductList();
                                    result.setId(franchise.getId());
                                    result.setName(franchise.getName());
                                    result.setBranches(brachList);
                                    return result;
                                })
                );
    }

    @Override
    public Mono<Franchise> updateName(Mono<Franchise> franchiseMono) {
        return franchiseMono
                .flatMap(franchise -> franchisePersistencePort.findById(franchise.getId())
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                .FRANCHISE_NOT_EXISTS)))
                        .flatMap(franExist -> franchisePersistencePort.findByName(franchise.getName())
                                .filter(exist -> !exist)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                        .FRANCHISE_ALREADY_EXISTS)))
                                .flatMap(ignore -> {
                                            franExist.setName(franchise.getName());
                                            return franchisePersistencePort.updateFranchise(Mono.just(franExist));
                                        })
                                )
                );
    }
}
