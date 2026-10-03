package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.util.ValidationCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.franchise.project.domain.BusinessErrors.businessError;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;

    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateBranchNameUseCase(branchPersistencePort, new ValidationCondition());
    }

    @Test
    void renamesTheBranchKeepingItsFranchise() {
        Branch renamed = new Branch(100L, "Uptown", 1L);
        when(branchPersistencePort.findById(100L)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));
        when(branchPersistencePort.existsByNameAndFranchiseId("Uptown", 1L)).thenReturn(Mono.just(false));
        when(branchPersistencePort.updateBranch(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(100L).name("Uptown").build()))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheBranchDoesNotExist() {
        when(branchPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(99L).name("Uptown").build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_NOT_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    @Test
    void failsWhenTheFranchiseAlreadyHasABranchWithThatName() {
        when(branchPersistencePort.findById(100L)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));
        when(branchPersistencePort.existsByNameAndFranchiseId("Airport", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(100L).name("Airport").build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).updateBranch(any());
    }
}
