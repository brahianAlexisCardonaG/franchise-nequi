package com.franchise.project.r2dbc.branch.repository;

import com.franchise.project.r2dbc.branch.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BranchReactiveRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Mono<Boolean> existsByNameAndFranchiseId(String name, Long franchiseId);
    Flux<BranchEntity> findByFranchiseId(Long franchiseId);
}
