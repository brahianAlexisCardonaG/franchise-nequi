package com.franchise.project.domain.branch.spi;

import com.franchise.project.domain.branch.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {
    Mono<Branch> createBranch(Branch branch);
    Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId);
    Mono<Branch> findById(Long id);
    Flux<Branch> findBranchesByFranchiseId(Long franchiseId);
    Mono<Branch> updateBranch(Branch branch);
}
