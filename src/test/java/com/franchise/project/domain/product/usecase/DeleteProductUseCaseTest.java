package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.franchise.project.domain.BusinessErrors.businessError;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productPersistencePort);
    }

    @Test
    void deletesAnExistingProduct() {
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productPersistencePort.deleteById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(100L))
                .verifyComplete();
        verify(productPersistencePort).deleteById(100L);
    }

    @Test
    void failsWhenTheProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(99L))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).deleteById(anyLong());
    }
}
