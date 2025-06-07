package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository;

import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
    Mono<FranchiseEntity> findByName(String name);
}
