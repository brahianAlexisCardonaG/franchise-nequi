package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.entity.FranchiseEntity;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper.FranchiseEntityMapper;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchisePersistenceAdapterTest {
    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private FranchiseEntityMapper franchiseEntityMapper;

    private FranchisePersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper);
    }

    // Métodos helper
    private Franchise getSampleFranchise() {
        return new Franchise(1L, "Test Franchise");
    }

    private FranchiseEntity getSampleFranchiseEntity() {
        FranchiseEntity entity = new FranchiseEntity();
        entity.setId(1L);
        entity.setName("Test Franchise");
        return entity;
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = getSampleFranchise();
        FranchiseEntity entity = getSampleFranchiseEntity();

        // Simular conversión y guardado
        when(franchiseEntityMapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.save(entity)).thenReturn(Mono.just(entity));
        when(franchiseEntityMapper.toModel(entity)).thenReturn(franchise);

        StepVerifier.create(adapter.createFranchise(Mono.just(franchise)))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueWhenFranchiseExistsByName() {
        String name = "Test Franchise";
        Franchise franchise = getSampleFranchise();
        FranchiseEntity entity = getSampleFranchiseEntity();

        when(franchiseRepository.findByName(name)).thenReturn(Mono.just(entity));
        when(franchiseEntityMapper.toModel(entity)).thenReturn(franchise);

        StepVerifier.create(adapter.findByName(name))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenFranchiseDoesNotExistByName() {
        String name = "NonExistent";

        when(franchiseRepository.findByName(name)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByName(name))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldFindFranchiseById() {
        Long id = 1L;
        Franchise franchise = getSampleFranchise();
        FranchiseEntity entity = getSampleFranchiseEntity();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(entity));
        when(franchiseEntityMapper.toModel(entity)).thenReturn(franchise);

        StepVerifier.create(adapter.findById(id))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void shouldUpdateFranchiseSuccessfully() {
        Franchise franchise = getSampleFranchise();
        FranchiseEntity entity = getSampleFranchiseEntity();

        when(franchiseEntityMapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.save(entity)).thenReturn(Mono.just(entity));
        when(franchiseEntityMapper.toModel(entity)).thenReturn(franchise);

        StepVerifier.create(adapter.updateFranchise(Mono.just(franchise)))
                .expectNext(franchise)
                .verifyComplete();
    }
}
