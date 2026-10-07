package com.franchise.project.r2dbc.franchise;

import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.r2dbc.franchise.entity.FranchiseEntity;
import com.franchise.project.r2dbc.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.r2dbc.franchise.repository.FranchiseReactiveRepository;
import com.franchise.project.r2dbc.resilience.PersistenceResilience;
import com.franchise.project.r2dbc.resilience.PersistenceResilienceProperties;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    private static final Franchise FRANCHISE = new Franchise(1L, "Franchise1");
    private static final FranchiseEntity ENTITY = new FranchiseEntity(1L, "Franchise1");

    @Mock
    private FranchiseReactiveRepository franchiseReactiveRepository;
    @Mock
    private FranchiseEntityMapper franchiseEntityMapper;

    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        PersistenceResilience persistenceResilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"),
                new PersistenceResilienceProperties("test", Duration.ofSeconds(1), 2, Duration.ofMillis(10), 16));
        adapter = new FranchiseRepositoryAdapter(franchiseReactiveRepository, franchiseEntityMapper, persistenceResilience);
    }

    @Test
    void createFranchiseSavesMappedEntity() {
        when(franchiseEntityMapper.toEntity(FRANCHISE)).thenReturn(ENTITY);
        when(franchiseReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(franchiseEntityMapper.toModel(ENTITY)).thenReturn(FRANCHISE);

        StepVerifier.create(adapter.createFranchise(FRANCHISE))
                .expectNext(FRANCHISE)
                .verifyComplete();
    }

    @Test
    void existsByNameReturnsWhetherFranchiseExists() {
        when(franchiseReactiveRepository.existsByName("Franchise1")).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsByName("Franchise1"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsMappedFranchise() {
        when(franchiseReactiveRepository.findById(1L)).thenReturn(Mono.just(ENTITY));
        when(franchiseEntityMapper.toModel(ENTITY)).thenReturn(FRANCHISE);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(FRANCHISE)
                .verifyComplete();
    }

    @Test
    void updateFranchiseSavesMappedEntity() {
        when(franchiseEntityMapper.toEntity(FRANCHISE)).thenReturn(ENTITY);
        when(franchiseReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(franchiseEntityMapper.toModel(ENTITY)).thenReturn(FRANCHISE);

        StepVerifier.create(adapter.updateFranchise(FRANCHISE))
                .expectNext(FRANCHISE)
                .verifyComplete();
    }
}
