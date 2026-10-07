package com.franchise.project.usecase.createbranch;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchFranchise;
import com.franchise.project.model.branch.gateways.BranchRepository;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.model.franchise.gateways.FranchiseRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private FranchiseRepository franchiseRepository;

    private CreateBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateBranchUseCase(branchRepository, franchiseRepository, new ValidationCondition());
    }

    @Test
    void createsTheBranchInsideItsFranchise() {
        Branch input = Branch.builder().name("Downtown").franchiseId(1L).build();
        Franchise franchise = new Franchise(1L, "Coffee House");
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchRepository.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(false));
        when(branchRepository.createBranch(input)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));

        StepVerifier.create(useCase.createBranch(input))
                .expectNext(new BranchFranchise(100L, "Downtown", franchise))
                .verifyComplete();
    }

    @Test
    void failsWhenTheFranchiseDoesNotExist() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createBranch(Branch.builder().name("Downtown").franchiseId(99L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(branchRepository, never()).existsByNameAndFranchiseId(anyString(), anyLong());
        verify(branchRepository, never()).createBranch(any());
    }

    @Test
    void failsWhenTheFranchiseAlreadyHasABranchWithThatName() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(branchRepository.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createBranch(Branch.builder().name("Downtown").franchiseId(1L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchRepository, never()).createBranch(any());
    }
}
