package com.franchise.project.domain.franchise.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.domain.franchise.spi.FranchisePersistencePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.spi.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchiseUseCaseTest {
    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    @Mock
    private BranchPersistencePort branchPersistencePort;

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    // Optional: Use this setup method if additional configuration is needed.
    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createFranchiseSuccess() {
        Franchise inputFranchise = new Franchise(null, "Franchise1");
        Franchise createdFranchise = new Franchise(1L, "Franchise1");

        // When searching for the franchise name, return false indicating that it does not exist.
        when(franchisePersistencePort.findByName("Franchise1")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.createFranchise(any(Mono.class)))
                .thenReturn(Mono.just(createdFranchise));

        Mono<Franchise> result = franchiseUseCase.createFranchise(Mono.just(inputFranchise));

        StepVerifier.create(result)
                .assertNext(franchise -> {
                    // Verify that the returned franchise is the created one.
                    org.junit.jupiter.api.Assertions.assertEquals(createdFranchise.getId(), franchise.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(createdFranchise.getName(), franchise.getName());
                })
                .verifyComplete();
    }

    @Test
    public void getFranchiseBranchProductSuccessWithProducts() {
        Long franchiseId = 1L;
        Franchise franchise = new Franchise(franchiseId, "Franchise1");

        // Create a branch belonging to the franchise.
        Branch branch = new Branch();
        branch.setId(10L);
        branch.setName("Branch10");
        branch.setFranchiseId(franchiseId);

        // Create two products for the branch.
        Product product1 = new Product(100L, "Product1", BigInteger.valueOf(10), branch.getId());
        Product product2 = new Product(101L, "Product2", BigInteger.valueOf(20), branch.getId());

        // Set up mocks.
        when(franchisePersistencePort.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.findBranchesByFranchiseId(franchiseId))
                .thenReturn(Mono.just(Collections.singletonList(branch)));
        when(productPersistencePort.findProductByBranchId(branch.getId()))
                .thenReturn(Mono.just(Arrays.asList(product1, product2)));

        Mono<FranchiseBranchProductList> result = franchiseUseCase.getFranchiseBranchProduct(franchiseId);

        StepVerifier.create(result)
                .assertNext(franchiseBranchProductList -> {
                    // Validate franchise info.
                    org.junit.jupiter.api.Assertions.assertEquals(franchise.getId(), franchiseBranchProductList.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(franchise.getName(), franchiseBranchProductList.getName());
                    // Validate that one branch exists.
                    org.junit.jupiter.api.Assertions.assertEquals(1, franchiseBranchProductList.getBranches().size());
                    BranchProduct branchProduct = franchiseBranchProductList.getBranches().get(0);
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getId(), branchProduct.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getName(), branchProduct.getName());
                    // Validate that the product with the maximum stock (product2) is selected.
                    org.junit.jupiter.api.Assertions.assertNotNull(branchProduct.getProduct());
                    org.junit.jupiter.api.Assertions.assertEquals(product2.getId(), branchProduct.getProduct().getId());
                })
                .verifyComplete();
    }

    // Success scenario: The franchise exists, a branch is found but no products exist for that branch;
    // the branch product should contain a null product.
    @Test
    public void getFranchiseBranchProductSuccessWithoutProducts() {
        Long franchiseId = 1L;
        Franchise franchise = new Franchise(franchiseId, "Franchise1");

        // Create a branch.
        Branch branch = new Branch();
        branch.setId(10L);
        branch.setName("Branch10");
        branch.setFranchiseId(franchiseId);

        // Set up mocks.
        when(franchisePersistencePort.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchPersistencePort.findBranchesByFranchiseId(franchiseId))
                .thenReturn(Mono.just(Collections.singletonList(branch)));
        // Return an empty list for products.
        when(productPersistencePort.findProductByBranchId(branch.getId()))
                .thenReturn(Mono.just(Collections.emptyList()));

        Mono<FranchiseBranchProductList> result = franchiseUseCase.getFranchiseBranchProduct(franchiseId);

        StepVerifier.create(result)
                .assertNext(franchiseBranchProductList -> {
                    org.junit.jupiter.api.Assertions.assertEquals(franchise.getId(), franchiseBranchProductList.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(franchise.getName(), franchiseBranchProductList.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(1, franchiseBranchProductList.getBranches().size());
                    BranchProduct branchProduct = franchiseBranchProductList.getBranches().get(0);
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getId(), branchProduct.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getName(), branchProduct.getName());
                    // Since there are no products, the product should be null.
                    org.junit.jupiter.api.Assertions.assertNull(branchProduct.getProduct());
                })
                .verifyComplete();
    }

    @Test
    public void updateNameSuccess() {
        Franchise inputFranchise = new Franchise(1L, "UpdatedName");
        Franchise existingFranchise = new Franchise(1L, "OldName");
        Franchise updatedFranchise = new Franchise(1L, "UpdatedName");

        when(franchisePersistencePort.findById(1L)).thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.findByName("UpdatedName")).thenReturn(Mono.just(false));
        when(franchisePersistencePort.updateFranchise(any(Mono.class)))
                .thenReturn(Mono.just(updatedFranchise));

        Mono<Franchise> result = franchiseUseCase.updateName(Mono.just(inputFranchise));

        StepVerifier.create(result)
                .assertNext(franchise -> {
                    org.junit.jupiter.api.Assertions.assertEquals(updatedFranchise.getId(), franchise.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("UpdatedName", franchise.getName());
                })
                .verifyComplete();
    }
}