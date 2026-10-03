package com.franchise.project.infrastructure.adapters.persistenceadapter.product;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.entity.ProductEntity;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductPersistenceAdapterTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper productEntityMapper;

    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductPersistenceAdapter(productRepository, productEntityMapper);
    }

    private Product getSampleProduct() {
        return new Product(1L, "Product A", BigInteger.TEN, 100L);
    }

    private ProductEntity getSampleProductEntity() {
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Product A");
        entity.setStock(BigInteger.TEN);
        entity.setBranchId(100L);
        return entity;
    }

    @Test
    void shouldCreateProductSuccessfully() {
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        Mono<Product> result = adapter.createProduct(product);

        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueWhenProductExistsByNameInBranch() {
        String name = "Product A";

        when(productRepository.existsByNameAndBranchId(name, 100L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameAndBranchId(name, 100L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenProductDoesNotExistByNameInBranch() {
        String name = "Nonexistent";

        when(productRepository.existsByNameAndBranchId(name, 100L)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByNameAndBranchId(name, 100L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldFindProductByIdSuccessfully() {
        Long id = 1L;
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productRepository.findById(id)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        StepVerifier.create(adapter.findById(id))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldDeleteProductByIdSuccessfully() {
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        Mono<Void> result = adapter.deleteById(1L);

        StepVerifier.create(result)
                .verifyComplete();
        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        Mono<Product> result = adapter.updateProduct(product);

        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldFindProductByBranchIdSuccessfully() {
        Long branchId = 100L;
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productRepository.findByBranchId(branchId)).thenReturn(Flux.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        Mono<List<Product>> result = adapter.findProductByBranchId(branchId);

        StepVerifier.create(result)
                .expectNext(List.of(product))
                .verifyComplete();
    }
}
