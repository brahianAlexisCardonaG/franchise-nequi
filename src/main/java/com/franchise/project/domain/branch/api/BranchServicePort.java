package com.franchise.project.domain.branch.api;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.product.model.Product;
import reactor.core.publisher.Mono;

public interface BranchServicePort {
    Mono<BranchFranchise> createBranch(Mono<Branch> branch);
    Mono<Branch> updateName(Mono<Branch> branchMono);
}
