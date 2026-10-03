package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
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
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    private UpdateProductNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductNameUseCase(productPersistencePort, new ValidationCondition());
    }

    @Test
    void changesOnlyTheName() {
        Product renamed = new Product(100L, "Double Espresso", 25, 10L);
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productPersistencePort.existsByNameAndBranchId("Double Espresso", 10L)).thenReturn(Mono.just(false));
        when(productPersistencePort.updateProduct(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateProductName(Product.builder().id(100L).name("Double Espresso").build()))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductName(Product.builder().id(99L).name("Double Espresso").build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void failsWhenTheBranchAlreadyHasAProductWithThatName() {
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productPersistencePort.existsByNameAndBranchId("Latte", 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateProductName(Product.builder().id(100L).name("Latte").build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }
}
