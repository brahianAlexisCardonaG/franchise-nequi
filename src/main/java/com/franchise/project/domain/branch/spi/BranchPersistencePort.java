package com.franchise.project.domain.branch.spi;

import com.franchise.project.domain.branch.model.Branch;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BranchPersistencePort {
    Mono<Branch> createBranch(Mono<Branch> branch);
    Mono<Boolean> existByName(String name);
    Mono<Branch> findById(Long id);
    Mono<List<Branch>> findBranchesByFranchiseId(Long franchiseId);
    Mono<Branch> updateBranch(Mono<Branch> branchMono);
}
