package com.franchise.project.infrastructure.adapters.persistenceadapter.branch;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.entity.BranchEntity;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
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

import java.time.Duration;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchPersistenceAdapterTest {

    private static final Branch BRANCH = new Branch(1L, "Downtown", 100L);
    private static final BranchEntity ENTITY = new BranchEntity(1L, "Downtown", 100L);

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private BranchEntityMapper branchEntityMapper;

    private BranchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        PersistenceResilience persistenceResilience = new PersistenceResilience(CircuitBreaker.ofDefaults("test"),
                new PersistenceResilienceProperties("test", Duration.ofSeconds(1), 2, Duration.ofMillis(10), 16));
        adapter = new BranchPersistenceAdapter(branchRepository, branchEntityMapper, persistenceResilience);
    }

    @Test
    void createBranchSavesMappedEntity() {
        when(branchEntityMapper.toEntity(BRANCH)).thenReturn(ENTITY);
        when(branchRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);

        StepVerifier.create(adapter.createBranch(BRANCH))
                .expectNext(BRANCH)
                .verifyComplete();
    }

    @Test
    void existsByNameAndFranchiseIdDelegatesToRepository() {
        when(branchRepository.existsByNameAndFranchiseId("Downtown", 100L)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByNameAndFranchiseId("Downtown", 100L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findByIdReturnsEmptyWhenBranchDoesNotExist() {
        when(branchRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99L))
                .verifyComplete();
    }

    @Test
    void findBranchesByFranchiseIdStreamsEveryBranch() {
        BranchEntity secondEntity = new BranchEntity(2L, "Airport", 100L);
        Branch secondBranch = new Branch(2L, "Airport", 100L);
        when(branchRepository.findByFranchiseId(100L)).thenReturn(Flux.just(ENTITY, secondEntity));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);
        when(branchEntityMapper.toModel(secondEntity)).thenReturn(secondBranch);

        StepVerifier.create(adapter.findBranchesByFranchiseId(100L))
                .expectNext(BRANCH, secondBranch)
                .verifyComplete();
    }

    @Test
    void updateBranchSavesMappedEntity() {
        when(branchEntityMapper.toEntity(BRANCH)).thenReturn(ENTITY);
        when(branchRepository.save(ENTITY)).thenReturn(Mono.just(ENTITY));
        when(branchEntityMapper.toModel(ENTITY)).thenReturn(BRANCH);

        StepVerifier.create(adapter.updateBranch(BRANCH))
                .expectNext(BRANCH)
                .verifyComplete();
    }
}
