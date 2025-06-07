package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.api.BranchServicePort;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase implements BranchServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final FranchisePersistencePort franchisePersistencePort;

    @Override
    public Mono<BranchFranchise> createBranch(Mono<Branch> branch) {
        return branch
                .flatMap(bran ->
                        branchPersistencePort.findByName(bran.getName())
                                .filter(exist -> !exist)
                                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                        .BRANCH_ALREADY_EXISTS)))
                                .flatMap(ignore ->
                                        branchPersistencePort.createBranch(Mono.just(bran))
                                                .flatMap(brandSave ->
                                                        franchisePersistencePort.findById(bran.getFranchiseId())
                                                                .flatMap(franExist -> {
                                                                    if(franExist == null){
                                                                       return Mono.error(new BusinessException(TechnicalMessage
                                                                                .BRANCH_NOT_EXISTS));
                                                                    }
                                                                    return Mono.just(franExist);
                                                                })
                                                                .flatMap(fran -> {
                                                                    BranchFranchise branchFranchise = new BranchFranchise();
                                                                    branchFranchise.setId(brandSave.getId());
                                                                    branchFranchise.setName(brandSave.getName());
                                                                    branchFranchise.setFranchise(fran);
                                                                    return Mono.just(branchFranchise);
                                                                }))
                                )
                );
    }
}
