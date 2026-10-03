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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    private UpdateProductStockUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProductStockUseCase(productPersistencePort, new ValidationCondition());
    }

    @Test
    void changesOnlyTheStock() {
        Product updated = new Product(100L, "Espresso", 80, 10L);
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productPersistencePort.updateProduct(updated)).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateProductStock(Product.builder().id(100L).stock(80).build()))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void failsWhenTheStockIsNegative() {
        StepVerifier.create(useCase.updateProductStock(Product.builder().id(100L).stock(-5).build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_STOCK_INVALID))
                .verify();
        verifyNoInteractions(productPersistencePort);
    }

    @Test
    void failsWhenTheProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProductStock(Product.builder().id(99L).stock(80).build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }
}
