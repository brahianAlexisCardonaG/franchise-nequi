package com.franchise.project.r2dbc.product;

import com.franchise.project.model.product.Product;
import com.franchise.project.r2dbc.product.entity.ProductEntity;
import com.franchise.project.r2dbc.product.mapper.ProductEntityMapper;
import com.franchise.project.r2dbc.product.repository.ProductReactiveRepository;
import com.franchise.project.r2dbc.resilience.PersistenceResilience;
import com.franchise.project.r2dbc.resilience.PersistenceResilienceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    private static final Product PRODUCT = new Product(1L, "Coffee", 10, 100L);
    private static final ProductEntity ENTITY = new ProductEntity(1L, "Coffee", 10, 100L);

    @Mock
    private ProductReactiveRepository productReactiveRepository;
    @Mock
    private ProductEntityMapper productEntityMapper;

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        PersistenceResilience persistenceResilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"),
                new PersistenceResilienceProperties("test", Duration.ofSeconds(1), 2, Duration.ofMillis(10), 16));
        adapter = new ProductRepositoryAdapter(productReactiveRepository, productEntityMapper, persistenceResilience);
    }

    @Test
    void createProductSavesMappedEntity() {
        when(productEntityMapper.toEntity(PRODUCT)).thenReturn(ENTITY);
        when(productReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.createProduct(PRODUCT))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void existsByNameAndBranchIdDelegatesToRepository() {
        when(productReactiveRepository.existsByNameAndBranchId("Coffee", 100L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameAndBranchId("Coffee", 100L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsMappedProduct() {
        when(productReactiveRepository.findById(1L)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void deleteByIdDeletesTheProduct() {
        when(productReactiveRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();
        verify(productReactiveRepository).deleteById(1L);
    }

    @Test
    void updateProductSavesMappedEntity() {
        when(productEntityMapper.toEntity(PRODUCT)).thenReturn(ENTITY);
        when(productReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.updateProduct(PRODUCT))
                .expectNext(PRODUCT)
                .verifyComplete();
    }

    @Test
    void findProductByBranchIdStreamsEveryProduct() {
        when(productReactiveRepository.findByBranchId(100L)).thenReturn(Flux.just(ENTITY));
        when(productEntityMapper.toModel(ENTITY)).thenReturn(PRODUCT);

        StepVerifier.create(adapter.findProductByBranchId(100L))
                .expectNext(PRODUCT)
                .verifyComplete();
    }
}
