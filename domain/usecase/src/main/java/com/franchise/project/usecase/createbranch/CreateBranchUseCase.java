package com.franchise.project.usecase.createbranch;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchFranchise;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
import com.franchise.project.usecase.validation.ValidationCondition;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateBranchUseCase {

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    private final ValidationCondition validationCondition;

    public Mono<BranchFranchise> createBranch(Branch branch) {
        return franchiseRepository.findById(branch.getFranchiseId())
                .switchIfEmpty(Mono.error(() -> new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)))
                .flatMap(franchise -> branchRepository.existsByNameAndFranchiseId(branch.getName(), franchise.getId())
                        .flatMap(exists -> validationCondition.rejectIfExists(exists, TechnicalMessage.BRANCH_ALREADY_EXISTS))
                        .then(Mono.defer(() -> branchRepository.createBranch(branch)))
                        .map(savedBranch -> BranchFranchise.builder()
                                .id(savedBranch.getId())
                                .name(savedBranch.getName())
                                .franchise(franchise)
                                .build()));
    }
}
