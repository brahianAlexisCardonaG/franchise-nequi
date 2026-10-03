package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
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

import java.math.BigInteger;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    private static final BigInteger STOCK = BigInteger.valueOf(50);
    private static final BigInteger NEGATIVE_STOCK = BigInteger.valueOf(-1);

    @Mock
    private BranchPersistencePort branchPersistencePort;
    @Mock
    private ProductPersistencePort productPersistencePort;

    private ProductUseCase productUseCase;

    @BeforeEach
    void setUp() {
        productUseCase = new ProductUseCase(branchPersistencePort, productPersistencePort, new ValidationCondition());
    }

    @Test
    void createProductReturnsProductWithItsBranch() {
        Product input = new Product(null, "Coffee", STOCK, 10L);
        Branch branch = new Branch(10L, "Downtown", 1L);
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Coffee", 10L)).thenReturn(Mono.just(false));
        when(productPersistencePort.createProduct(input)).thenReturn(Mono.just(new Product(100L, "Coffee", STOCK, 10L)));

        StepVerifier.create(productUseCase.createProduct(input))
                .expectNext(new ProductBranch(100L, "Coffee", STOCK, branch))
                .verifyComplete();
    }

    @Test
    void createProductFailsWhenStockIsNegative() {
        StepVerifier.create(productUseCase.createProduct(new Product(null, "Coffee", NEGATIVE_STOCK, 10L)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_STOCK_INVALID))
                .verify();
        verifyNoInteractions(branchPersistencePort, productPersistencePort);
    }

    @Test
    void createProductFailsWhenBranchDoesNotExist() {
        when(branchPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.createProduct(new Product(null, "Coffee", STOCK, 99L)))
                .expectErrorMatches(businessError(TechnicalMessage.BRANCH_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).createProduct(any());
    }

    @Test
    void createProductFailsWhenNameAlreadyExistsInBranch() {
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(new Branch(10L, "Downtown", 1L)));
        when(productPersistencePort.existsByNameAndBranchId("Coffee", 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.createProduct(new Product(null, "Coffee", STOCK, 10L)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();
        verify(productPersistencePort, never()).createProduct(any());
    }

    @Test
    void deleteProductBranchDeletesExistingProduct() {
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Coffee", STOCK, 10L)));
        when(productPersistencePort.deleteById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductBranch(100L))
                .verifyComplete();
        verify(productPersistencePort).deleteById(100L);
    }

    @Test
    void deleteProductBranchFailsWhenProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProductBranch(99L))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).deleteById(anyLong());
    }

    @Test
    void updateStockChangesOnlyTheStock() {
        Product expectedUpdate = new Product(100L, "Coffee", BigInteger.valueOf(80), 10L);
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Coffee", STOCK, 10L)));
        when(productPersistencePort.updateProduct(expectedUpdate)).thenReturn(Mono.just(expectedUpdate));

        StepVerifier.create(productUseCase.updateStock(new Product(100L, null, BigInteger.valueOf(80), null)))
                .expectNext(expectedUpdate)
                .verifyComplete();
    }

    @Test
    void updateStockFailsWhenStockIsNegative() {
        StepVerifier.create(productUseCase.updateStock(new Product(100L, null, NEGATIVE_STOCK, null)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_STOCK_INVALID))
                .verify();
        verifyNoInteractions(productPersistencePort);
    }

    @Test
    void updateStockFailsWhenProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(new Product(99L, null, STOCK, null)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void updateNameChangesOnlyTheName() {
        Product expectedUpdate = new Product(100L, "Espresso", STOCK, 10L);
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Coffee", STOCK, 10L)));
        when(productPersistencePort.existsByNameAndBranchId("Espresso", 10L)).thenReturn(Mono.just(false));
        when(productPersistencePort.updateProduct(expectedUpdate)).thenReturn(Mono.just(expectedUpdate));

        StepVerifier.create(productUseCase.updateName(new Product(100L, "Espresso", null, null)))
                .expectNext(expectedUpdate)
                .verifyComplete();
    }

    @Test
    void updateNameFailsWhenProductDoesNotExist() {
        when(productPersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateName(new Product(99L, "Espresso", null, null)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_NOT_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }

    @Test
    void updateNameFailsWhenNameAlreadyExistsInBranch() {
        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(new Product(100L, "Coffee", STOCK, 10L)));
        when(productPersistencePort.existsByNameAndBranchId("Tea", 10L)).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.updateName(new Product(100L, "Tea", null, null)))
                .expectErrorMatches(businessError(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                .verify();
        verify(productPersistencePort, never()).updateProduct(any());
    }

    private static Predicate<Throwable> businessError(TechnicalMessage expected) {
        return error -> error instanceof BusinessException businessException
                && businessException.getTechnicalMessage() == expected;
    }
}
