package com.franchise.project.api.product.handler;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.usecase.createproduct.CreateProductUseCase;
import com.franchise.project.usecase.deleteproduct.DeleteProductUseCase;
import com.franchise.project.usecase.updateproductname.UpdateProductNameUseCase;
import com.franchise.project.usecase.updateproductstock.UpdateProductStockUseCase;
import com.franchise.project.model.product.Product;
import com.franchise.project.model.product.ProductBranch;
import com.franchise.project.api.product.RouterRestProduct;
import com.franchise.project.api.product.mapper.ProductMapperImpl;
import com.franchise.project.api.product.mapper.ProductMapperResponseImpl;
import com.franchise.project.api.util.validation.RequestValidator;
import jakarta.validation.Validation;
import com.franchise.project.api.util.error.ApplyErrorHandler;
import com.franchise.project.api.util.error.BuildErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    private static final Integer STOCK = 10;

    @Mock
    private CreateProductUseCase createProductUseCase;
    @Mock
    private DeleteProductUseCase deleteProductUseCase;
    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;
    @Mock
    private UpdateProductNameUseCase updateProductNameUseCase;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductHandler handler = new ProductHandler(new RequestValidator(Validation.buildDefaultValidatorFactory().getValidator()), new ProductMapperImpl(),
                new ProductMapperResponseImpl(), createProductUseCase, deleteProductUseCase,
                updateProductStockUseCase, updateProductNameUseCase, new ApplyErrorHandler(new BuildErrorResponse()));
        webTestClient = WebTestClient.bindToRouterFunction(new RouterRestProduct().routerFunctionProduct(handler)).build();
    }

    @Test
    void createProductReturnsCreated() {
        when(createProductUseCase.createProduct(new Product(null, "Coffee", STOCK, 5L)))
                .thenReturn(Mono.just(new ProductBranch(1L, "Coffee", STOCK, new Branch(5L, "Downtown", 1L))));

        webTestClient.post().uri("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Coffee\",\"stock\":10,\"branchId\":5}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_CREATED.getCode())
                .jsonPath("$.data.name").isEqualTo("Coffee")
                .jsonPath("$.data.branch.id").isEqualTo(5);
    }

    @Test
    void createProductWithoutStockReturnsBadRequest() {
        webTestClient.post().uri("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Coffee\",\"branchId\":5}")
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(createProductUseCase, deleteProductUseCase, updateProductStockUseCase, updateProductNameUseCase);
    }

    @Test
    void createProductWithNegativeStockReturnsBadRequest() {
        when(createProductUseCase.createProduct(new Product(null, "Coffee", -3, 5L)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_STOCK_INVALID)));

        webTestClient.post().uri("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Coffee\",\"stock\":-3,\"branchId\":5}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_STOCK_INVALID.getMessage());
    }

    @Test
    void createProductRaceOnUniqueConstraintReturnsConflict() {
        when(createProductUseCase.createProduct(new Product(null, "Coffee", STOCK, 5L)))
                .thenReturn(Mono.error(new DuplicateKeyException("uq_product_branch_name")));

        webTestClient.post().uri("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Coffee\",\"stock\":10,\"branchId\":5}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.RESOURCE_ALREADY_EXISTS.getMessage());
    }

    @Test
    void deleteProductReturnsOk() {
        when(deleteProductUseCase.deleteProduct(99L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/product/{productId}", 99)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_DELETED.getMessage());
    }

    @Test
    void deleteUnknownProductReturnsNotFound() {
        when(deleteProductUseCase.deleteProduct(99L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)));

        webTestClient.delete().uri("/api/v1/product/{productId}", 99)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteProductWithNonNumericIdReturnsBadRequest() {
        webTestClient.delete().uri("/api/v1/product/{productId}", "abc")
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(createProductUseCase, deleteProductUseCase, updateProductStockUseCase, updateProductNameUseCase);
    }

    @Test
    void updateProductStockReturnsOk() {
        when(updateProductStockUseCase.updateProductStock(new Product(1L, null, 50, null)))
                .thenReturn(Mono.just(new Product(1L, "Coffee", 50, 5L)));

        webTestClient.put().uri("/api/v1/product/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":1,\"stock\":50}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.stock").isEqualTo(50);
    }

    @Test
    void updateStockOfUnknownProductReturnsNotFound() {
        when(updateProductStockUseCase.updateProductStock(new Product(99L, null, 50, null)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)));

        webTestClient.put().uri("/api/v1/product/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":99,\"stock\":50}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateProductNameReturnsOk() {
        when(updateProductNameUseCase.updateProductName(new Product(1L, "Espresso", null, null)))
                .thenReturn(Mono.just(new Product(1L, "Espresso", STOCK, 5L)));

        webTestClient.put().uri("/api/v1/product/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":1,\"name\":\"Espresso\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.name").isEqualTo("Espresso");
    }

    @Test
    void updateProductNameWithDuplicatedNameReturnsConflict() {
        when(updateProductNameUseCase.updateProductName(new Product(1L, "Tea", null, null)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXISTS)));

        webTestClient.put().uri("/api/v1/product/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":1,\"name\":\"Tea\"}")
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}
