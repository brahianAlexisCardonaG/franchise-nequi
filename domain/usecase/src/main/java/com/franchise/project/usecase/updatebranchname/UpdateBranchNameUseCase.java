package com.franchise.project.usecase.updatebranchname;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {

    private final BranchRepository branchRepository;
    private final ValidationCondition validationCondition;

    public Mono<Branch> updateBranchName(Branch branch) {
        return branchRepository.findById(branch.getId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.BRANCH_NOT_EXISTS)))
                .flatMap(existing -> branchRepository.existsByNameAndFranchiseId(branch.getName(), existing.getFranchiseId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                        .then(Mono.defer(() -> branchRepository.updateBranch(
                                existing.toBuilder().name(branch.getName()).build()))));
    }
}
