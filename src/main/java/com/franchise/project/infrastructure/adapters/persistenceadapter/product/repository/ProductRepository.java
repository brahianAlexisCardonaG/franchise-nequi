package com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository;

import com.franchise.project.infrastructure.adapters.persistenceadapter.product.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Mono<ProductEntity> findByName(String name);
}
