package com.franchise.project.infrastructure.adapters.persistenceadapter.branch;

import com.franchise.project.domain.branch.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchPersistencePort {
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;


    @Override
    public Mono<Branch> createBranch(Mono<Branch> branch) {
        return branch
                .map(branchEntityMapper::toEntity)
                .flatMap(branchRepository::save)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> findByName(String name) {
        return branchRepository.findByName(name)
                .map(branchEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }
}
