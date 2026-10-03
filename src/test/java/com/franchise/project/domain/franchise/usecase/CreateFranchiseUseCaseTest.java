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
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateFranchiseUseCase(franchisePersistencePort, new ValidationCondition());
    }

    @Test
    void createsTheFranchiseWhenTheNameIsFree() {
        Franchise input = Franchise.builder().name("Coffee House").build();
        Franchise created = new Franchise(1L, "Coffee House");
        when(franchisePersistencePort.existsByName("Coffee House")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.createFranchise(input)).thenReturn(Mono.just(created));

        StepVerifier.create(useCase.createFranchise(input))
                .expectNext(created)
                .verifyComplete();
    }

    @Test
    void failsWhenTheNameAlreadyExists() {
        when(franchisePersistencePort.existsByName("Coffee House")).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createFranchise(Franchise.builder().name("Coffee House").build()))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).createFranchise(any());
    }
}
