package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.api.CreateBranchServicePort;
import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateBranchUseCase implements CreateBranchServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final FranchisePersistencePort franchisePersistencePort;
    private final ValidationCondition validationCondition;

    @Override
    public Mono<BranchFranchise> createBranch(Branch branch) {
        return franchisePersistencePort.findById(branch.getFranchiseId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise -> branchPersistencePort.existsByNameAndFranchiseId(branch.getName(), franchise.getId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                        .then(Mono.defer(() -> branchPersistencePort.createBranch(branch)))
                        .map(savedBranch -> BranchFranchise.builder()
                                .id(savedBranch.getId())
                                .name(savedBranch.getName())
                                .franchise(franchise)
                                .build()));
    }
}
