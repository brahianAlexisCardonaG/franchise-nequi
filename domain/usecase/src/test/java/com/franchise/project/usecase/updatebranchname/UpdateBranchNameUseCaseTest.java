package com.franchise.project.usecase.updatebranchname;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.usecase.validation.ValidationCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.franchise.project.usecase.BusinessErrors.businessError;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    private UpdateBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateBranchNameUseCase(branchRepository, new ValidationCondition());
    }

    @Test
    void renamesTheBranchKeepingItsFranchise() {
        Branch renamed = new Branch(100L, "Uptown", 1L);
        when(branchRepository.findById(100L)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));
        when(branchRepository.existsByNameAndFranchiseId("Uptown", 1L)).thenReturn(Mono.just(false));
        when(branchRepository.updateBranch(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(100L).name("Uptown").build()))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheBranchDoesNotExist() {
        when(branchRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(99L).name("Uptown").build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_NOT_EXISTS))
                .verify();
        verify(branchRepository, never()).updateBranch(any());
    }

    @Test
    void failsWhenTheFranchiseAlreadyHasABranchWithThatName() {
        when(branchRepository.findById(100L)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));
        when(branchRepository.existsByNameAndFranchiseId("Airport", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateBranchName(Branch.builder().id(100L).name("Airport").build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchRepository, never()).updateBranch(any());
    }
}
