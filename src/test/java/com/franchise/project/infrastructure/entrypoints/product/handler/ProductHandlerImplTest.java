package com.franchise.project.infrastructure.entrypoints.product.handler;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.product.api.ProductServicePort;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.domain.product.model.ProductBranch;
import com.franchise.project.infrastructure.entrypoints.product.RouterRestProduct;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapperImpl;
import com.franchise.project.infrastructure.entrypoints.product.mapper.ProductMapperResponseImpl;
import com.franchise.project.infrastructure.entrypoints.product.validations.ProductValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.error.BuildErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerImplTest {

    private static final BigInteger STOCK = BigInteger.TEN;

    @Mock
    private ProductServicePort productServicePort;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductHandlerImpl handler = new ProductHandlerImpl(new ProductValidationDto(), new ProductMapperImpl(),
                new ProductMapperResponseImpl(), productServicePort, new ApplyErrorHandler(new BuildErrorResponse()));
        webTestClient = WebTestClient.bindToRouterFunction(new RouterRestProduct().routerFunctionProduct(handler)).build();
    }

    @Test
    void createProductReturnsCreated() {
        when(productServicePort.createProduct(new Product(null, "Coffee", STOCK, 5L)))
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
        verifyNoInteractions(productServicePort);
    }

    @Test
    void createProductWithNegativeStockReturnsBadRequest() {
        when(productServicePort.createProduct(new Product(null, "Coffee", BigInteger.valueOf(-3), 5L)))
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
        when(productServicePort.createProduct(new Product(null, "Coffee", STOCK, 5L)))
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
        when(productServicePort.deleteProductBranch(99L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v1/product/{productId}", 99)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_BRANCH_DELETE.getMessage());
    }

    @Test
    void deleteUnknownProductReturnsNotFound() {
        when(productServicePort.deleteProductBranch(99L))
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
        verifyNoInteractions(productServicePort);
    }

    @Test
    void updateProductStockReturnsOk() {
        when(productServicePort.updateStock(new Product(1L, null, BigInteger.valueOf(50), null)))
                .thenReturn(Mono.just(new Product(1L, "Coffee", BigInteger.valueOf(50), 5L)));

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
        when(productServicePort.updateStock(new Product(99L, null, BigInteger.valueOf(50), null)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NOT_EXISTS)));

        webTestClient.put().uri("/api/v1/product/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":99,\"stock\":50}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateProductNameReturnsOk() {
        when(productServicePort.updateName(new Product(1L, "Espresso", null, null)))
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
        when(productServicePort.updateName(new Product(1L, "Tea", null, null)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXISTS)));

        webTestClient.put().uri("/api/v1/product/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":1,\"name\":\"Tea\"}")
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}
