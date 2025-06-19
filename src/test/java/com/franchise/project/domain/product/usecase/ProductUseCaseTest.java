package com.franchise.project.domain.product.usecase;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.spi.BranchPersistencePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductUseCaseTest {
    @Mock
    private BranchPersistencePort branchPersistencePort;

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private ProductUseCase productUseCase;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createProductSuccess() {
        Product product = new Product();
        product.setName("Product1");
        product.setStock(BigInteger.valueOf(50));
        product.setBranchId(10L);

        // Stub branch for the provided branchId.
        Branch branch = new Branch();
        branch.setId(10L);
        branch.setName("Branch10");

        // Simulate an already non-existing product.
        when(productPersistencePort.findByName("Product1")).thenReturn(Mono.just(false));
        // Simulate that the branch exists.
        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(branch));

        // Simulate the creation of the product in the persistence layer.
        Product savedProduct = new Product(100L, "Product1", BigInteger.valueOf(50), product.getBranchId());
        when(productPersistencePort.createProduct(any(Mono.class))).thenReturn(Mono.just(savedProduct));

        Mono<ProductBranch> result = productUseCase.createProduct(Mono.just(product));

        StepVerifier.create(result)
                .assertNext(productBranch -> {
                    // Validate ProductBranch details.
                    org.junit.jupiter.api.Assertions.assertEquals(savedProduct.getId(), productBranch.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(savedProduct.getName(), productBranch.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(savedProduct.getStock(), productBranch.getStock());
                    org.junit.jupiter.api.Assertions.assertNotNull(productBranch.getBranch());
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getId(), productBranch.getBranch().getId());
                    org.junit.jupiter.api.Assertions.assertEquals(branch.getName(), productBranch.getBranch().getName());
                })
                .verifyComplete();
    }



    @Test
    public void deleteProductBranchSuccess() {
        Long productId = 100L;
        Product product = new Product(productId, "Product1", BigInteger.valueOf(50), 10L);

        // Simulate that the product exists.
        when(productPersistencePort.findById(productId)).thenReturn(Mono.just(product));
        // Simulate deletion returns an empty Mono.
        when(productPersistencePort.deleteRelateProductBranch(any(Mono.class))).thenReturn(Mono.empty());

        Mono<Void> result = productUseCase.deleteProductBranch(productId);

        StepVerifier.create(result)
                .verifyComplete();
    }


    @Test
    public void updateStockSuccess() {
        // Input product with the new stock.
        Product inputProduct = new Product(100L, "Product1", BigInteger.valueOf(100), 10L);
        // Existing product with old stock.
        Product existingProduct = new Product(100L, "Product1", BigInteger.valueOf(50), 10L);
        // Updated product after update operation.
        Product updatedProduct = new Product(100L, "Product1", BigInteger.valueOf(100), 10L);

        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.updateProduct(any(Mono.class))).thenReturn(Mono.just(updatedProduct));

        Mono<Product> result = productUseCase.updateStock(Mono.just(inputProduct));

        StepVerifier.create(result)
                .assertNext(prod -> {
                    org.junit.jupiter.api.Assertions.assertEquals(updatedProduct.getStock(), prod.getStock());
                })
                .verifyComplete();
    }


    @Test
    public void updateNameSuccess() {
        // Input product with the new name.
        Product inputProduct = new Product(100L, "UpdatedProduct", BigInteger.valueOf(50), 10L);
        // Existing product before update.
        Product existingProduct = new Product(100L, "OldProduct", BigInteger.valueOf(50), 10L);
        // Updated product after renaming.
        Product updatedProduct = new Product(100L, "UpdatedProduct", BigInteger.valueOf(50), 10L);

        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(existingProduct));
        // Simulate that the new name does not already exist.
        when(productPersistencePort.findByName("UpdatedProduct")).thenReturn(Mono.just(false));
        when(productPersistencePort.updateProduct(any(Mono.class))).thenReturn(Mono.just(updatedProduct));

        Mono<Product> result = productUseCase.updateName(Mono.just(inputProduct));

        StepVerifier.create(result)
                .assertNext(prod -> {
                    org.junit.jupiter.api.Assertions.assertEquals("UpdatedProduct", prod.getName());
                })
                .verifyComplete();
    }
}
