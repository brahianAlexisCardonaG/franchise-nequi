package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.api.UpdateBranchNameServicePort;
import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase implements UpdateBranchNameServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<Branch> updateBranchName(Branch branch) {
        return branchPersistencePort.findById(branch.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.BRANCH_NOT_EXISTS)))
                .flatMap(existing -> branchPersistencePort.existsByNameAndFranchiseId(branch.getName(), existing.getFranchiseId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                        .then(Mono.defer(() -> branchPersistencePort.updateBranch(
                                existing.toBuilder().name(branch.getName()).build()))));
    }
}
