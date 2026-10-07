package com.franchise.project.usecase.deleteproduct;

import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.franchise.project.usecase.BusinessErrors.businessError;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productRepository);
    }

    @Test
    void deletesAnExistingProduct() {
        when(productRepository.findById(100L)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));
        when(productRepository.deleteById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(100L))
                .verifyComplete();
        verify(productRepository).deleteById(100L);
    }

    @Test
    void failsWhenTheProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(99L))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productRepository, never()).deleteById(anyLong());
    }
}
