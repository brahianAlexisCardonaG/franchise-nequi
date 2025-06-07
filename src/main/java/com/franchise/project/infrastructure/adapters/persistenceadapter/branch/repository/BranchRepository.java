package com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository;

import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BranchRepository extends ReactiveCrudRepository<BranchEntity, Long> {
    Mono<BranchEntity> findByName(String name);
}
