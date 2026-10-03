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
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(productEntityMapper.toEntity(product))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, Long branchId) {
        return productRepository.existsByNameAndBranchId(name, branchId);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    public Mono<Product> updateProduct(Product product) {
        return productRepository.save(productEntityMapper.toEntity(product))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<List<Product>> findProductByBranchId(Long branchId) {
        return productRepository.findByBranchId(branchId)
                .map(productEntityMapper::toModel)
                .collectList();
    }

}
