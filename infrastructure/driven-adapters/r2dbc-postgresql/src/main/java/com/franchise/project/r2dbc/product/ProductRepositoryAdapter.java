package com.franchise.project.r2dbc.product;

import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import com.franchise.project.r2dbc.product.mapper.ProductEntityMapper;
import com.franchise.project.r2dbc.product.repository.ProductReactiveRepository;
import com.franchise.project.r2dbc.resilience.PersistenceResilience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductReactiveRepository productReactiveRepository;
    private final ProductEntityMapper productEntityMapper;
    private final PersistenceResilience persistenceResilience;

    @Override
    public Mono<Product> createProduct(Product product) {
        return persistenceResilience.write(productReactiveRepository.save(productEntityMapper.toEntity(product)))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, Long branchId) {
        return persistenceResilience.read(productReactiveRepository.existsByNameAndBranchId(name, branchId));
    }

    @Override
    public Mono<Product> findById(Long id) {
        return persistenceResilience.read(productReactiveRepository.findById(id))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return persistenceResilience.write(productReactiveRepository.deleteById(id));
    }

    @Override
    public Mono<Product> updateProduct(Product product) {
        return persistenceResilience.write(productReactiveRepository.save(productEntityMapper.toEntity(product)))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Flux<Product> findProductByBranchId(Long branchId) {
        return persistenceResilience.read(productReactiveRepository.findByBranchId(branchId))
                .map(productEntityMapper::toModel);
    }
}
