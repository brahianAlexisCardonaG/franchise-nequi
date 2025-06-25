package com.franchise.project.infrastructure.adapters.persistenceadapter.branch;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.entity.BranchEntity;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper.BranchEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchPersistenceAdapterTest {
    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchEntityMapper branchEntityMapper;

    private BranchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BranchPersistenceAdapter(branchRepository, branchEntityMapper);
    }

    private Branch getSampleBranch() {
        return new Branch(1L, "Sucursal Central", 100L);
    }

    private BranchEntity getSampleEntity() {
        BranchEntity entity = new BranchEntity();
        entity.setId(1L);
        entity.setName("Sucursal Central");
        entity.setFranchiseId(100L);
        return entity;
    }

    @Test
    void shouldCreateBranchSuccessfully() {
        Branch branch = getSampleBranch();
        BranchEntity entity = getSampleEntity();

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toModel(entity)).thenReturn(branch);

        Mono<Branch> result = adapter.createBranch(branch);

        StepVerifier.create(result)
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueWhenBranchExistsByName() {
        String branchName = "Sucursal Central";
        Branch branch = getSampleBranch();
        BranchEntity entity = getSampleEntity();

        when(branchRepository.findByName(branchName)).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toModel(entity)).thenReturn(branch);

        Mono<Boolean> result = adapter.existByName(branchName);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenBranchDoesNotExistByName() {
        String branchName = "NonExistent";

        when(branchRepository.findByName(branchName)).thenReturn(Mono.empty());

        Mono<Boolean> result = adapter.existByName(branchName);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldFindBranchById() {
        Long branchId = 1L;
        Branch branch = getSampleBranch();
        BranchEntity entity = getSampleEntity();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toModel(entity)).thenReturn(branch);

        Mono<Branch> result = adapter.findById(branchId);

        StepVerifier.create(result)
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void shouldFindBranchesByFranchiseId() {
        Long franchiseId = 100L;
        Branch branch = getSampleBranch();
        BranchEntity entity = getSampleEntity();

        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(entity));
        when(branchEntityMapper.toModel(entity)).thenReturn(branch);

        Mono<List<Branch>> result = adapter.findBranchesByFranchiseId(franchiseId);

        StepVerifier.create(result)
                .expectNext(List.of(branch))
                .verifyComplete();
    }

    @Test
    void shouldUpdateBranchSuccessfully() {
        Branch branch = getSampleBranch();
        BranchEntity entity = getSampleEntity();

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toModel(entity)).thenReturn(branch);

        Mono<Branch> result = adapter.updateBranch(branch);

        StepVerifier.create(result)
                .expectNext(branch)
                .verifyComplete();
    }
}
