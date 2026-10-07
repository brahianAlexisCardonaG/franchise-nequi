package com.franchise.project.r2dbc.product.repository;

import com.franchise.project.r2dbc.product.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);
    Flux<ProductEntity> findByBranchId(Long branchId);
}
