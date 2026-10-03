package com.franchise.project.domain.branch.api;

import com.franchise.project.domain.branch.model.Branch;
import reactor.core.publisher.Mono;

public interface UpdateBranchNameServicePort {
    Mono<Branch> updateBranchName(Branch branch);
}
