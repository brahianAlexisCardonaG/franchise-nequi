package com.franchise.project.domain.franchise.usecase;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    private UpdateFranchiseNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateFranchiseNameUseCase(franchisePersistencePort, new ValidationCondition());
    }

    @Test
    void renamesTheFranchise() {
        Franchise renamed = new Franchise(1L, "Coffee House Express");
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(franchisePersistencePort.existsByName("Coffee House Express")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.updateFranchise(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(1L, "Coffee House Express")))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheFranchiseDoesNotExist() {
        when(franchisePersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(99L, "Coffee House Express")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void failsWhenTheNewNameIsTaken() {
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(franchisePersistencePort.existsByName("Tea House")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(1L, "Tea House")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }
}
