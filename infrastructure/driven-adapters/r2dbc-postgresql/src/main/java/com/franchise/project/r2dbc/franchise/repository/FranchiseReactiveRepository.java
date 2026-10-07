package com.franchise.project.r2dbc.franchise.repository;

import com.franchise.project.r2dbc.franchise.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FranchiseReactiveRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {
    Mono<Boolean> existsByName(String name);
}
