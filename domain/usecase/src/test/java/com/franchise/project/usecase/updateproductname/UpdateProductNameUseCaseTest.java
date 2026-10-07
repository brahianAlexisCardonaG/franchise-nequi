package com.franchise.project.usecase.updateproductname;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
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
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private UpdateProductNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductNameUseCase(productRepository, new ValidationCondition());
    }

    @Test
    void changesOnlyTheName() {
        Product renamed = new Product(100L, "Double Espresso", 25, 10L);
        when(productRepository.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productRepository.existsByNameAndBranchId("Double Espresso", 10L)).thenReturn(Mono.just(false));
        when(productRepository.updateProduct(renamed)).thenReturn(Mono.just(renamed));

        StepVerifier.create(useCase.updateProductName(Product.builder().id(100L).name("Double Espresso").build()))
                .expectNext(renamed)
                .verifyComplete();
    }

    @Test
    void failsWhenTheProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductName(Product.builder().id(99L).name("Double Espresso").build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productRepository, never()).updateProduct(any());
    }

    @Test
    void failsWhenTheBranchAlreadyHasAProductWithThatName() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productRepository.existsByNameAndBranchId("Latte", 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.updateProductName(Product.builder().id(100L).name("Latte").build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();
        verify(productRepository, never()).updateProduct(any());
    }
}
