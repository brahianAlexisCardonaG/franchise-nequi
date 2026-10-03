package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBranchUseCaseTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;
    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    private CreateBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBranchUseCase(branchPersistencePort, franchisePersistencePort, new ValidationCondition());
    }

    @Test
    void createsTheBranchInsideItsFranchise() {
        Branch input = Branch.builder().name("Downtown").franchiseId(1L).build();
        Franchise franchise = new Franchise(1L, "Coffee House");
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(false));
        when(branchPersistencePort.createBranch(input)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));

        StepVerifier.create(useCase.createBranch(input))
                .expectNext(new BranchFranchise(100L, "Downtown", franchise))
                .verifyComplete();
    }

    @Test
    void failsWhenTheFranchiseDoesNotExist() {
        when(franchisePersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createBranch(Branch.builder().name("Downtown").franchiseId(99L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).existsByNameAndFranchiseId(anyString(), anyLong());
        verify(branchPersistencePort, never()).createBranch(any());
    }

    @Test
    void failsWhenTheFranchiseAlreadyHasABranchWithThatName() {
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(branchPersistencePort.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createBranch(Branch.builder().name("Downtown").franchiseId(1L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).createBranch(any());
    }
}
