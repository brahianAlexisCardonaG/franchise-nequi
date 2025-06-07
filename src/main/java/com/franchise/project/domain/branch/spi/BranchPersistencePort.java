package com.franchise.project.domain.branch.spi;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.franchise.model.Franchise;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {
    Mono<Branch> createBranch(Mono<Branch> branch);
    Mono<Boolean> findByName(String name);
    Mono<Branch> findById(Long id);
}
