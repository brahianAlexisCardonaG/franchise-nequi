package com.franchise.project.model.franchise.gateways;

import com.franchise.project.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Mono<Franchise> createFranchise(Franchise franchise);
    Mono<Boolean> existsByName(String name);
    Mono<Franchise> findById(Long id);
    Mono<Franchise> updateFranchise(Franchise franchise);
}
