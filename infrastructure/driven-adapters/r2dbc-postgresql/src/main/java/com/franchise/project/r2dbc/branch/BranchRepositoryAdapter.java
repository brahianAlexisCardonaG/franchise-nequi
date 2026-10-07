package com.franchise.project.r2dbc.branch;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.r2dbc.branch.mapper.BranchEntityMapper;
import com.franchise.project.r2dbc.branch.repository.BranchReactiveRepository;
import com.franchise.project.r2dbc.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepository {
    private final BranchReactiveRepository branchReactiveRepository;
    private final BranchEntityMapper branchEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Branch> createBranch(Branch branch) {
        return persistenceResilience.write(branchReactiveRepository.save(branchEntityMapper.toEntity(branch)))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId) {
        return persistenceResilience.read(branchReactiveRepository.existsByNameAndFranchiseId(name, franchiseId));
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return persistenceResilience.read(branchReactiveRepository.findById(id))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Flux<Branch> findBranchesByFranchiseId(Long franchiseId) {
        return persistenceResilience.read(branchReactiveRepository.findByFranchiseId(franchiseId))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> updateBranch(Branch branch) {
        return persistenceResilience.write(branchReactiveRepository.save(branchEntityMapper.toEntity(branch)))
                .map(branchEntityMapper::toModel);
    }
}
