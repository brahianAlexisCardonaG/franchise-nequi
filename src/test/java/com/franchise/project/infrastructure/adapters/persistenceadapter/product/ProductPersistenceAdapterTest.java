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

    // Métodos helper para generar instancias de Product y ProductEntity
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

        // Simulamos el mapeo y la operación de guardado
        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        Mono<Product> result = adapter.createProduct(Mono.just(product));

        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueWhenProductExistsByName() {
        String name = "Product A";
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productRepository.findByName(name)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        StepVerifier.create(adapter.findByName(name))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenProductDoesNotExistByName() {
        String name = "Nonexistent";

        when(productRepository.findByName(name)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName(name))
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
    void shouldDeleteRelateProductBranchSuccessfully() {
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        // En este flujo se simula el mapeo y "guardado" para finalmente completar sin valor (Mono<Void>)
        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));

        Mono<Void> result = adapter.deleteRelateProductBranch(Mono.just(product));

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product product = getSampleProduct();
        ProductEntity entity = getSampleProductEntity();

        when(productEntityMapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(Mono.just(entity));
        when(productEntityMapper.toModel(entity)).thenReturn(product);

        Mono<Product> result = adapter.updateProduct(Mono.just(product));

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
