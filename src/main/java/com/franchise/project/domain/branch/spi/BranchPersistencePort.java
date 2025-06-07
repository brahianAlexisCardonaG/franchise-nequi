package com.franchise.project.domain.branch.spi;

import com.franchise.project.domain.branch.Branch;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {
    Mono<Branch> createBranch(Mono<Branch> branch);
    Mono<Boolean> findByName(String name);
}
