package com.franchise.project.usecase.createfranchise;

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
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateFranchiseUseCase(franchiseRepository, new ValidationCondition());
    }

    @Test
    void createsTheFranchiseWhenTheNameIsFree() {
        Franchise input = Franchise.builder().name("Coffee House").build();
        Franchise created = new Franchise(1L, "Coffee House");
        when(franchiseRepository.existsByName("Coffee House")).thenReturn(Mono.just(false));
        when(franchiseRepository.createFranchise(input)).thenReturn(Mono.just(created));

        StepVerifier.create(useCase.createFranchise(input))
                .expectNext(created)
                .verifyComplete();
    }

    @Test
    void failsWhenTheNameAlreadyExists() {
        when(franchiseRepository.existsByName("Coffee House")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createFranchise(Franchise.builder().name("Coffee House").build()))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchiseRepository, never()).createFranchise(any());
    }
}
