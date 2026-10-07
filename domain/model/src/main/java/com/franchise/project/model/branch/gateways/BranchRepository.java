package com.franchise.project.model.branch.gateways;

import com.franchise.project.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> createBranch(Branch branch);
    Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId);
    Mono<Branch> findById(Long id);
    Flux<Branch> findBranchesByFranchiseId(Long franchiseId);
    Mono<Branch> updateBranch(Branch branch);
}
