package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
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
class CreateProductUseCaseTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;
    @Mock
    private ProductPersistencePort productPersistencePort;

    private CreateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateProductUseCase(branchPersistencePort, productPersistencePort, new ValidationCondition());
    }

    @Test
    void createsTheProductInsideItsBranch() {
        Product input = Product.builder().name("Espresso").stock(25).branchId(10L).build();
        Branch branch = new Branch(10L, "Downtown", 1L);
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Espresso", 10L)).thenReturn(Mono.just(false));
        when(productPersistencePort.createProduct(input)).thenReturn(Mono.just(new Product(100L, "Espresso", 25, 10L)));

        StepVerifier.create(useCase.createProduct(input))
                .expectNext(new ProductBranch(100L, "Espresso", 25, branch))
                .verifyComplete();
    }

    @Test
    void acceptsZeroStock() {
        Product input = Product.builder().name("Espresso").stock(0).branchId(10L).build();
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(new Branch(10L, "Downtown", 1L)));
        when(productPersistencePort.existsByNameAndBranchId("Espresso", 10L)).thenReturn(Mono.just(false));
        when(productPersistencePort.createProduct(input)).thenReturn(Mono.just(new Product(100L, "Espresso", 0, 10L)));

        StepVerifier.create(useCase.createProduct(input))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void failsWhenTheStockIsNegative() {
        StepVerifier.create(useCase.createProduct(Product.builder().name("Espresso").stock(-1).branchId(10L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_STOCK_INVALID))
                .verify();
        verifyNoInteractions(branchPersistencePort, productPersistencePort);
    }

    @Test
    void failsWhenTheBranchDoesNotExist() {
        when(branchPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createProduct(Product.builder().name("Espresso").stock(25).branchId(99L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).createProduct(any());
    }

    @Test
    void failsWhenTheBranchAlreadyHasAProductWithThatName() {
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(new Branch(10L, "Downtown", 1L)));
        when(productPersistencePort.existsByNameAndBranchId("Espresso", 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.createProduct(Product.builder().name("Espresso").stock(25).branchId(10L).build()))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();
        verify(productPersistencePort, never()).createProduct(any());
    }
}
