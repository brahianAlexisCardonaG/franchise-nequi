package com.franchise.project.infrastructure.adapters.persistenceadapter.product;

import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.entity.ProductEntity;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.mapper.ProductEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.product.repository.ProductRepository;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilience;
import com.franchise.project.infrastructure.adapters.persistenceadapter.resilience.PersistenceResilienceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    private static final Product PRODUCT = new Product(1L, "Coffee", BigInteger.TEN, 100L);
    private static final ProductEntity ENTITY = new ProductEntity(1L, "Coffee", BigInteger.TEN, 100L);

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductEntityMapper productEntityMapper;

    private ProductPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        PersistenceResilience persistenceResilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"),
                new PersistenceResilienceProperties("test", Duration.ofSeconds(1), 2, Duration.ofMillis(10), 16));
        adapter = new ProductPersistenceAdapter(productRepository, productEntityMapper, persistenceResilience);
    }

    @Test
    void createProductSavesMappedEntity() {
        when(productEntityMapper.toEntity(PRODUCT)).thenReturn(ENTITY);
        when(productRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.createProduct(PRODUCT))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void existsByNameAndBranchIdDelegatesToRepository() {
        when(productRepository.existsByNameAndBranchId("Coffee", 100L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameAndBranchId("Coffee", 100L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsMappedProduct() {
        when(productRepository.findById(1L)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void deleteByIdDeletesTheProduct() {
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();
        verify(productRepository).deleteById(1L);
    }

    @Test
    void updateProductSavesMappedEntity() {
        when(productEntityMapper.toEntity(PRODUCT)).thenReturn(ENTITY);
        when(productRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.updateProduct(PRODUCT))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void findProductByBranchIdStreamsEveryProduct() {
        when(productRepository.findByBranchId(100L)).thenReturn(Flux.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.findProductByBranchId(100L))
                .expectNext(PRODUCT)
                .verifyComplete();
    }
}
