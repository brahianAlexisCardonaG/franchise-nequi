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
    @Mock private BranchPersistencePort branchPersistencePort;
    @Mock private ProductPersistencePort productPersistencePort;
    @Mock private ValidationCondition validationCondition;

    @InjectMocks
    private ProductUseCase productUseCase;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createProductSuccess() {
        Product product = new Product(null, "Product1", BigInteger.valueOf(50), 10L);
        Branch branch = new Branch(10L, "Branch10", 1L);
        Product savedProduct = new Product(100L, "Product1", BigInteger.valueOf(50), 10L);

        when(productPersistencePort.findByName("Product1")).thenReturn(Mono.just(false));
        when(validationCondition.validationExist(false, TechnicalMessage.PRODUCT_ALREADY_EXISTS)).thenReturn(Mono.empty());

        when(branchPersistencePort.findById(10L)).thenReturn(Mono.just(branch));
        when(validationCondition.validationExist(false, TechnicalMessage.BRANCH_NOT_EXISTS)).thenReturn(Mono.empty());

        when(productPersistencePort.createProduct(product)).thenReturn(Mono.just(savedProduct));

        Mono<ProductBranch> result = productUseCase.createProduct(product);

        StepVerifier.create(result)
                .assertNext(pb -> {
                    org.junit.jupiter.api.Assertions.assertEquals(savedProduct.getId(), pb.getId());
                    org.junit.jupiter.api.Assertions.assertEquals("Product1", pb.getName());
                    org.junit.jupiter.api.Assertions.assertEquals(BigInteger.valueOf(50), pb.getStock());
                    org.junit.jupiter.api.Assertions.assertEquals(branch, pb.getBranch());
                })
                .verifyComplete();
    }

    @Test
    public void deleteProductBranchSuccess() {
        Long productId = 100L;
        Product product = new Product(productId, "Product1", BigInteger.valueOf(50), 10L);

        when(productPersistencePort.findById(productId)).thenReturn(Mono.just(product));
        when(validationCondition.validationExist(false, TechnicalMessage.PRODUCT_NOT_EXISTS)).thenReturn(Mono.empty());
        when(productPersistencePort.deleteRelateProductBranch(any(Product.class))).thenReturn(Mono.empty());

        Mono<Void> result = productUseCase.deleteProductBranch(productId);

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    public void updateStockSuccess() {
        Product inputProduct = new Product(100L, "Product1", BigInteger.valueOf(100), 10L);
        Product existingProduct = new Product(100L, "Product1", BigInteger.valueOf(50), 10L);
        Product updatedProduct = new Product(100L, "Product1", BigInteger.valueOf(100), 10L);

        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(existingProduct));
        when(validationCondition.validationExist(false, TechnicalMessage.PRODUCT_NOT_EXISTS)).thenReturn(Mono.empty());
        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        Mono<Product> result = productUseCase.updateStock(inputProduct);

        StepVerifier.create(result)
                .assertNext(prod -> {
                    org.junit.jupiter.api.Assertions.assertEquals(BigInteger.valueOf(100), prod.getStock());
                })
                .verifyComplete();
    }

    @Test
    public void updateNameSuccess() {
        Product inputProduct = new Product(100L, "UpdatedProduct", BigInteger.valueOf(50), 10L);
        Product existingProduct = new Product(100L, "OldProduct", BigInteger.valueOf(50), 10L);
        Product updatedProduct = new Product(100L, "UpdatedProduct", BigInteger.valueOf(50), 10L);

        when(productPersistencePort.findById(100L)).thenReturn(Mono.just(existingProduct));
        when(validationCondition.validationExist(false, TechnicalMessage.PRODUCT_NOT_EXISTS)).thenReturn(Mono.empty());

        when(productPersistencePort.findByName("UpdatedProduct")).thenReturn(Mono.just(false));
        when(validationCondition.validationExist(false, TechnicalMessage.PRODUCT_ALREADY_EXISTS)).thenReturn(Mono.empty());

        when(productPersistencePort.updateProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        Mono<Product> result = productUseCase.updateName(inputProduct);

        StepVerifier.create(result)
                .assertNext(prod -> org.junit.jupiter.api.Assertions.assertEquals("UpdatedProduct", prod.getName()))
                .verifyComplete();
    }
}
