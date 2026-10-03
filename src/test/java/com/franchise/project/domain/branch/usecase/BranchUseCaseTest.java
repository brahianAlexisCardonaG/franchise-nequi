package com.franchise.project.domain.branch.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
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

import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;
    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    private BranchUseCase branchUseCase;

    @BeforeEach
    void setUp() {
        branchUseCase = new BranchUseCase(branchPersistencePort, franchisePersistencePort, new ValidationCondition());
    }

    @Test
    void createBranchReturnsBranchWithItsFranchise() {
        Branch input = new Branch(null, "Downtown", 1L);
        Franchise franchise = new Franchise(1L, "Franchise1");
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(false));
        when(branchPersistencePort.createBranch(input)).thenReturn(Mono.just(new Branch(100L, "Downtown", 1L)));

        StepVerifier.create(branchUseCase.createBranch(input))
                .expectNext(new BranchFranchise(100L, "Downtown", franchise))
                .verifyComplete();
    }

    @Test
    void createBranchFailsWhenFranchiseDoesNotExist() {
        when(franchisePersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.createBranch(new Branch(null, "Downtown", 99L)))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).existsByNameAndFranchiseId(anyString(), anyLong());
        verify(branchPersistencePort, never()).createBranch(any());
    }

    @Test
    void createBranchFailsWhenNameAlreadyExistsInFranchise() {
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Franchise1")));
        when(branchPersistencePort.existsByNameAndFranchiseId("Downtown", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(branchUseCase.createBranch(new Branch(null, "Downtown", 1L)))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).createBranch(any());
    }

    @Test
    void updateNameKeepsFranchiseAndChangesName() {
        Branch expectedUpdate = new Branch(100L, "UpdatedName", 1L);
        when(branchPersistencePort.findById(100L)).thenReturn(Mono.just(new Branch(100L, "OldName", 1L)));
        when(branchPersistencePort.existsByNameAndFranchiseId("UpdatedName", 1L)).thenReturn(Mono.just(false));
        when(branchPersistencePort.updateBranch(expectedUpdate)).thenReturn(Mono.just(expectedUpdate));

        StepVerifier.create(branchUseCase.updateName(new Branch(100L, "UpdatedName", null)))
                .expectNext(expectedUpdate)
                .verifyComplete();
    }

    @Test
    void updateNameFailsWhenBranchDoesNotExist() {
        when(branchPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateName(new Branch(99L, "UpdatedName", null)))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_NOT_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    @Test
    void updateNameFailsWhenNameAlreadyExistsInFranchise() {
        when(branchPersistencePort.findById(100L)).thenReturn(Mono.just(new Branch(100L, "OldName", 1L)));
        when(branchPersistencePort.existsByNameAndFranchiseId("Taken", 1L)).thenReturn(Mono.just(true));

        StepVerifier.create(branchUseCase.updateName(new Branch(100L, "Taken", null)))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_ALREADY_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).updateBranch(any());
    }

    private static Predicate<Throwable> businessError(TechnicalMessage expected) {
        return error -> error instanceof BusinessException businessException
                && businessException.getTechnicalMessage() == expected;
    }
}
