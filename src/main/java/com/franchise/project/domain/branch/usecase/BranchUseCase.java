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
                                        franchisePersistencePort.findById(bran.getFranchiseId())
                                                .switchIfEmpty(Mono
                                                        .error(new BusinessException(TechnicalMessage
                                                                .FRANCHISE_NOT_EXISTS)))
                                )
                                .flatMap(fran ->
                                        branchPersistencePort.createBranch(Mono.just(bran))
                                                .flatMap(branchSaved -> {
                                                    BranchFranchise branchFranchise = new BranchFranchise();
                                                    branchFranchise.setId(branchSaved.getId());
                                                    branchFranchise.setName(branchSaved.getName());
                                                    branchFranchise.setFranchise(fran);
                                                    return Mono.just(branchFranchise);
                                                }))
                );
    }

    @Override
    public Mono<Branch> updateName(Mono<Branch> branchMono) {
        return branchMono
                .flatMap(branch -> branchPersistencePort.findById(branch.getId())
                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                .BRANCH_NOT_EXISTS)))

                        .flatMap(branExist ->
                                branchPersistencePort.findByName(branch.getName())
                                        .filter(exist -> !exist)
                                        .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage
                                                .BRANCH_ALREADY_EXISTS)))
                                        .flatMap(ignore -> {
                                            branExist.setName(branch.getName());
                                            return branchPersistencePort.updateProduct(Mono.just(branExist));
                                        }))
                );
    }
}
