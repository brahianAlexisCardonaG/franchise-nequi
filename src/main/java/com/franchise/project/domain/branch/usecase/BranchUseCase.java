package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.api.BranchServicePort;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase implements BranchServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final FranchisePersistencePort franchisePersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<BranchFranchise> createBranch(Branch branch) {
        return branchPersistencePort.existByName(branch.getName())
                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .then(Mono.defer(() ->
                        franchisePersistencePort.findById(branch.getFranchiseId())
                                .flatMap(fran -> validationCondition.validationExist(fran == null, TechnicalMessage.FRANCHISE_NOT_EXISTS)
                                        .thenReturn(fran))
                                .flatMap(fran ->
                                        branchPersistencePort.createBranch(branch)
                                                .map(branchSaved -> new BranchFranchise(
                                                        branchSaved.getId(),
                                                        branchSaved.getName(),
                                                        fran
                                                ))
                                )
                ));
    }


    @Override
    public Mono<Branch> updateName(Branch branch) {
        return branchPersistencePort.findById(branch.getId())
                .flatMap(existing ->
                        validationCondition.validationExist(existing == null, TechnicalMessage.BRANCH_NOT_EXISTS)
                                .thenReturn(existing)
                )
                .flatMap(existing ->
                        branchPersistencePort.existByName(branch.getName())
                                .flatMap(exist -> validationCondition.validationExist(exist, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                                .then(Mono.defer(() -> {
                                    Branch updated = new Branch(
                                            existing.getId(),
                                            branch.getName(),
                                            existing.getFranchiseId()
                                    );
                                    return branchPersistencePort.updateBranch(updated);
                                }))
                );
    }
}