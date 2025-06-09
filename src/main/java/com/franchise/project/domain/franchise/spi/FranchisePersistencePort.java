package com.franchise.project.domain.franchise.spi;

import com.franchise.project.domain.franchise.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchisePersistencePort {
    Mono<Franchise> createFranchise(Mono<Franchise> franchise);
    Mono<Boolean> findByName(String name);
    Mono<Franchise> findById(Long id);
    Mono<Franchise> updateFranchise(Mono<Franchise> franchiseMono);
}
