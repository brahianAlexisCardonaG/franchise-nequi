package com.franchise.project.infrastructure.adapters.persistenceadapter.branch;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<Boolean> existByName(String name) {
        return branchRepository.findByName(name)
                .map(branchEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepository.findById(id)
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<List<Branch>> findBranchesByFranchiseId(Long franchiseId) {
        return branchRepository.findByFranchiseId(franchiseId)
                .map(branchEntityMapper::toModel)
                .collectList();
    }

    @Override
    public Mono<Branch> updateBranch(Mono<Branch> branchMono) {
        return branchMono
                .map(branchEntityMapper::toEntity)
                .flatMap(branchRepository::save)
                .map(branchEntityMapper::toModel);
    }
}
