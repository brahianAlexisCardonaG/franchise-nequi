package com.franchise.project.domain.branch.api;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<BranchFranchise> createBranch(Mono<Branch> branch);
}
