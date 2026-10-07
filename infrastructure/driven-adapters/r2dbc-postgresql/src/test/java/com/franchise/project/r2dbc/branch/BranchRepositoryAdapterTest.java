package com.franchise.project.r2dbc.branch;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.r2dbc.branch.entity.BranchEntity;
import com.franchise.project.r2dbc.branch.mapper.BranchEntityMapper;
import com.franchise.project.r2dbc.branch.repository.BranchReactiveRepository;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchRepositoryAdapterTest {

    private static final Branch BRANCH = new Branch(1L, "Downtown", 100L);
    private static final BranchEntity ENTITY = new BranchEntity(1L, "Downtown", 100L);

    @Mock
    private BranchReactiveRepository branchReactiveRepository;
    @Mock
    private BranchEntityMapper branchEntityMapper;

    private BranchRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        PersistenceResilience persistenceResilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"),
                new PersistenceResilienceProperties("test", Duration.ofSeconds(1), 2, Duration.ofMillis(10), 16));
        adapter = new BranchRepositoryAdapter(branchReactiveRepository, branchEntityMapper, persistenceResilience);
    }

    @Test
    void createBranchSavesMappedEntity() {
        when(branchEntityMapper.toEntity(BRANCH)).thenReturn(ENTITY);
        when(branchReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);

        StepVerifier.create(adapter.createBranch(BRANCH))
                .expectNext(BRANCH)
                .verifyComplete();
    }

    @Test
    void existsByNameAndFranchiseIdDelegatesToRepository() {
        when(branchReactiveRepository.existsByNameAndFranchiseId("Downtown", 100L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameAndFranchiseId("Downtown", 100L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsEmptyWhenBranchDoesNotExist() {
        when(branchReactiveRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99L))
                .verifyComplete();
    }

    @Test
    void findBranchesByFranchiseIdStreamsEveryBranch() {
        BranchEntity secondEntity = new BranchEntity(2L, "Airport", 100L);
        Branch secondBranch = new Branch(2L, "Airport", 100L);
        when(branchReactiveRepository.findByFranchiseId(100L)).thenReturn(Flux.just(ENTITY, secondEntity));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);
        when(branchEntityMapper.toModel(secondEntity)).thenReturn(secondBranch);

        StepVerifier.create(adapter.findBranchesByFranchiseId(100L))
                .expectNext(BRANCH, secondBranch)
                .verifyComplete();
    }

    @Test
    void updateBranchSavesMappedEntity() {
        when(branchEntityMapper.toEntity(BRANCH)).thenReturn(ENTITY);
        when(branchReactiveRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);

        StepVerifier.create(adapter.updateBranch(BRANCH))
                .expectNext(BRANCH)
                .verifyComplete();
    }
}
