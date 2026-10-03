package com.franchise.project.infrastructure.adapters.persistenceadapter.branch;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchPersistenceAdapter implements BranchPersistencePort {
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Branch> createBranch(Branch branch) {
        return persistenceResilience.write(branchRepository.save(branchEntityMapper.toEntity(branch)))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId) {
        return persistenceResilience.read(branchRepository.existsByNameAndFranchiseId(name, franchiseId));
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return persistenceResilience.read(branchRepository.findById(id))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Flux<Branch> findBranchesByFranchiseId(Long franchiseId) {
        return persistenceResilience.read(branchRepository.findByFranchiseId(franchiseId))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> updateBranch(Branch branch) {
        return persistenceResilience.write(branchRepository.save(branchEntityMapper.toEntity(branch)))
                .map(branchEntityMapper::toModel);
    }
}
