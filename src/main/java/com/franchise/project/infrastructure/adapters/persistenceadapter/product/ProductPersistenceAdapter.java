package com.franchise.project.infrastructure.adapters.persistenceadapter.product;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Product> createProduct(Product product) {
        return persistenceResilience.write(productRepository.save(productEntityMapper.toEntity(product)))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, Long branchId) {
        return persistenceResilience.read(productRepository.existsByNameAndBranchId(name, branchId));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return persistenceResilience.read(productRepository.findById(id))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return persistenceResilience.write(productRepository.deleteById(id));
    }

    @Override
    public Mono<Product> updateProduct(Product product) {
        return persistenceResilience.write(productRepository.save(productEntityMapper.toEntity(product)))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Flux<Product> findProductByBranchId(Long branchId) {
        return persistenceResilience.read(productRepository.findByBranchId(branchId))
                .map(productEntityMapper::toModel);
    }
}
