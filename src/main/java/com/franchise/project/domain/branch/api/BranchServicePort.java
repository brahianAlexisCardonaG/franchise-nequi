package com.franchise.project.domain.branch.api;

import com.franchise.project.domain.branch.Branch;
import com.franchise.project.domain.branch.BranchFranchise;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<BranchFranchise> createBranch(Mono<Branch> branch);
}
