package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchUseCaseTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private BranchUseCase branchUseCase;

    @Test
    public void createBranchSuccess() {
        Branch branch = new Branch();
        branch.setName("Branch1");
        branch.setFranchiseId(1L);

        Franchise franchise = new Franchise(1L, "Franchise1");
        Branch branchCreated = new Branch(100L, "Branch1", 1L);

        when(branchPersistencePort.existByName("Branch1")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.createBranch(any(Mono.class))).thenReturn(Mono.just(branchCreated));

        Mono<BranchFranchise> result = branchUseCase.createBranch(Mono.just(branch));

        StepVerifier.create(result)
                .assertNext(branchFranchise -> {
                    org.junit.jupiter.api.Assertions.assertEquals(branchCreated.getId(), branchFranchise.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(branchCreated.getName(), branchFranchise.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(franchise, branchFranchise.getFranchise());
                })
                .verifyComplete();
    }

    @Test
    public void updateNameSuccess() {
        Branch branchToUpdate = new Branch(100L, "UpdatedName", 1L);
        Branch branchExisting = new Branch(100L, "OldName", 1L);
        Branch branchUpdated = new Branch(100L, "UpdatedName", 1L);

        when(branchPersistencePort.findById(100L)).thenReturn(Mono.just(branchExisting));
        when(branchPersistencePort.existByName("UpdatedName")).thenReturn(Mono.just(false));
        when(branchPersistencePort.updateBranch(any(Mono.class))).thenReturn(Mono.just(branchUpdated));
        Mono<Branch> result = branchUseCase.updateName(Mono.just(branchToUpdate));

        StepVerifier.create(result)
                .assertNext(branch -> org.junit.jupiter.api.Assertions.assertEquals("UpdatedName", branch.getName()))
                .verifyComplete();
    }
}

