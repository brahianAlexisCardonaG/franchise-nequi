package com.franchise.project.domain.franchise.api;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> createFranchise(Franchise franchise);
    Mono<FranchiseBranchProductList> getFranchiseBranchProduct(Long franchiseId);
    Mono<Franchise> updateName(Franchise franchiseMono);
}
