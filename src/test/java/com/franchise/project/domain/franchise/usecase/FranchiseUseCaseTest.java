package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import com.franchise.project.domain.util.ValidationCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchisePersistencePort franchisePersistencePort;
    @Mock
    private BranchPersistencePort branchPersistencePort;
    @Mock
    private ProductPersistencePort productPersistencePort;

    private FranchiseUseCase franchiseUseCase;

    @BeforeEach
    void setUp() {
        franchiseUseCase = new FranchiseUseCase(franchisePersistencePort, branchPersistencePort,
                productPersistencePort, new ValidationCondition());
    }

    @Test
    void createFranchiseReturnsCreatedFranchise() {
        Franchise input = new Franchise(null, "Franchise1");
        Franchise created = new Franchise(1L, "Franchise1");
        when(franchisePersistencePort.findByName("Franchise1")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.createFranchise(input)).thenReturn(Mono.just(created));

        StepVerifier.create(franchiseUseCase.createFranchise(input))
                .expectNext(created)
                .verifyComplete();
    }

    @Test
    void createFranchiseFailsWhenNameAlreadyExists() {
        Franchise input = new Franchise(null, "Franchise1");
        when(franchisePersistencePort.findByName("Franchise1")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseUseCase.createFranchise(input))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).createFranchise(any());
    }

    @Test
    void getFranchiseBranchProductReturnsLargestStockProductPerBranch() {
        Franchise franchise = new Franchise(1L, "Franchise1");
        Branch branchWithProducts = new Branch(10L, "Downtown", 1L);
        Branch emptyBranch = new Branch(11L, "Airport", 1L);
        Product lowStock = new Product(100L, "Water", BigInteger.valueOf(10), 10L);
        Product highStock = new Product(101L, "Coffee", BigInteger.valueOf(20), 10L);

        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.findBranchesByFranchiseId(1L)).thenReturn(Flux.just(branchWithProducts, emptyBranch));
        when(productPersistencePort.findProductByBranchId(10L)).thenReturn(Flux.just(lowStock, highStock));
        when(productPersistencePort.findProductByBranchId(11L)).thenReturn(Flux.empty());

        StepVerifier.create(franchiseUseCase.getFranchiseBranchProduct(1L))
                .assertNext(result -> {
                    assertEquals(1L, result.getId());
                    assertEquals(2, result.getBranches().size());
                    BranchProduct downtown = result.getBranches().get(0);
                    assertEquals(10L, downtown.getId());
                    assertEquals(highStock, downtown.getProduct());
                    BranchProduct airport = result.getBranches().get(1);
                    assertEquals(11L, airport.getId());
                    assertNull(airport.getProduct());
                })
                .verifyComplete();
    }

    @Test
    void getFranchiseBranchProductFailsWhenFranchiseDoesNotExist() {
        when(franchisePersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getFranchiseBranchProduct(99L))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(branchPersistencePort, never()).findBranchesByFranchiseId(anyLong());
    }

    @Test
    void updateNameReturnsUpdatedFranchise() {
        Franchise updated = new Franchise(1L, "UpdatedName");
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "OldName")));
        when(franchisePersistencePort.findByName("UpdatedName")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.updateFranchise(updated)).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseUseCase.updateName(new Franchise(1L, "UpdatedName")))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void updateNameFailsWhenFranchiseDoesNotExist() {
        when(franchisePersistencePort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateName(new Franchise(99L, "UpdatedName")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_NOT_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    @Test
    void updateNameFailsWhenNewNameAlreadyExists() {
        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "OldName")));
        when(franchisePersistencePort.findByName("Taken")).thenReturn(Mono.just(true));

        StepVerifier.create(franchiseUseCase.updateName(new Franchise(1L, "Taken")))
                .expectErrorMatches(businessError(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                .verify();
        verify(franchisePersistencePort, never()).updateFranchise(any());
    }

    private static Predicate<Throwable> businessError(TechnicalMessage expected) {
        return error -> error instanceof BusinessException businessException
                && businessException.getTechnicalMessage() == expected;
    }
}
