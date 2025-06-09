package com.franchise.project.infrastructure.adapters.persistenceadapter.product;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public Mono<Product> createProduct(Mono<Product> product) {
        return product
                .map(productEntityMapper::toEntity)
                .flatMap(productRepository::save)
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> findByName(String name) {
        return productRepository.findByName(name)
                .map(productEntityMapper::toModel)
                .map(tech -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteRelateProductBranch(Mono<Product> product) {
        return product
                .map(productEntityMapper::toEntity)
                .flatMap(productRepository::save)
                .then();
    }

    @Override
    public Mono<Product> updateProductStock(Mono<Product> product) {
        return product
                .map(productEntityMapper::toEntity)
                .flatMap(productRepository::save)
                .map(productEntityMapper::toModel);
    }

    @Override
    public Flux<Product> findProductByBranchId(Long branchId) {
        return productRepository.findByBranchId(branchId)
                .map(productEntityMapper::toModel);
    }

}
