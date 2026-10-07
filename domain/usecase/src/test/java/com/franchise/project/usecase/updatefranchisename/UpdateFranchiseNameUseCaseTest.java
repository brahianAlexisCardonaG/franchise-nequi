package com.franchise.project.usecase.updatefranchisename;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private UpdateFranchiseNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateFranchiseNameUseCase(franchiseRepository, new ValidationCondition());
    }

    @Test
    void renamesTheFranchise() {
        Franchise renamed = new Franchise(1L, "Coffee House Express");
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(franchiseRepository.existsByName("Coffee House Express")).thenReturn(Mono.just(false));
        when(franchiseRepository.updateFranchise(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(1L, "Coffee House Express")))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheFranchiseDoesNotExist() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(99L, "Coffee House Express")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(franchiseRepository, never()).updateFranchise(any());
    }

    @Test
    void failsWhenTheNewNameIsTaken() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "Coffee House")));
        when(franchiseRepository.existsByName("Tea House")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateFranchiseName(new Franchise(1L, "Tea House")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchiseRepository, never()).updateFranchise(any());
    }
}
