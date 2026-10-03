package com.franchise.project.infrastructure.entrypoints.franchise.handler;

import com.franchise.project.domain.branch.model.BranchProduct;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.domain.product.model.Product;
import com.franchise.project.infrastructure.entrypoints.franchise.RouterRestFranchise;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapperImpl;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapperResponseImpl;
import com.franchise.project.infrastructure.entrypoints.util.validation.RequestValidator;
import jakarta.validation.Validation;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.error.BuildErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerImplTest {

    @Mock
    private FranchiseServicePort franchiseServicePort;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        FranchiseHandlerImpl handler = new FranchiseHandlerImpl(new RequestValidator(Validation.buildDefaultValidatorFactory().getValidator()), new FranchiseMapperImpl(),
                new FranchiseMapperResponseImpl(), franchiseServicePort, new ApplyErrorHandler(new BuildErrorResponse()));
        webTestClient = WebTestClient.bindToRouterFunction(new RouterRestFranchise().routerFunctionFranchise(handler)).build();
    }

    @Test
    void createFranchiseReturnsCreated() {
        when(franchiseServicePort.createFranchise(new Franchise(null, "Franchise1")))
                .thenReturn(Mono.just(new Franchise(1L, "Franchise1")));

        webTestClient.post().uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Franchise1\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_CREATED.getCode())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Franchise1");
    }

    @Test
    void createFranchiseWithoutNameReturnsBadRequest() {
        webTestClient.post().uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(franchiseServicePort);
    }

    @Test
    void createFranchiseWithBlankNameReturnsBadRequest() {
        webTestClient.post().uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"   \"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.INVALID_PARAMETERS.getMessage());
        verifyNoInteractions(franchiseServicePort);
    }

    @Test
    void createFranchiseWithDuplicatedNameReturnsConflict() {
        when(franchiseServicePort.createFranchise(new Franchise(null, "Franchise1")))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS)));

        webTestClient.post().uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Franchise1\"}")
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void getTopStockProductsReturnsLargestStockProductPerBranch() {
        Product coffee = new Product(100L, "Coffee", 20, 10L);
        when(franchiseServicePort.getFranchiseBranchProduct(1L)).thenReturn(Mono.just(new FranchiseBranchProductList(
                1L, "Franchise1", List.of(new BranchProduct(10L, "Downtown", coffee)))));

        webTestClient.get().uri("/api/v1/franchise/{franchiseId}/top-stock-products", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getCode())
                .jsonPath("$.data.branches[0].name").isEqualTo("Downtown")
                .jsonPath("$.data.branches[0].product.name").isEqualTo("Coffee")
                .jsonPath("$.data.branches[0].product.stock").isEqualTo(20);
    }

    @Test
    void getTopStockProductsWithNonNumericIdReturnsBadRequest() {
        webTestClient.get().uri("/api/v1/franchise/{franchiseId}/top-stock-products", "abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.INVALID_PARAMETERS.getMessage());
        verifyNoInteractions(franchiseServicePort);
    }

    @Test
    void getTopStockProductsForUnknownFranchiseReturnsNotFound() {
        when(franchiseServicePort.getFranchiseBranchProduct(99L))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)));

        webTestClient.get().uri("/api/v1/franchise/{franchiseId}/top-stock-products", 99)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateFranchiseNameReturnsOk() {
        when(franchiseServicePort.updateName(new Franchise(1L, "Renamed")))
                .thenReturn(Mono.just(new Franchise(1L, "Renamed")));

        webTestClient.put().uri("/api/v1/franchise/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":1,\"name\":\"Renamed\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_UPDATE.getCode())
                .jsonPath("$.data.name").isEqualTo("Renamed");
    }

    @Test
    void unexpectedErrorReturnsGenericInternalServerError() {
        when(franchiseServicePort.getFranchiseBranchProduct(1L))
                .thenReturn(Mono.error(new IllegalStateException("sensitive internal detail")));

        webTestClient.get().uri("/api/v1/franchise/{franchiseId}/top-stock-products", 1)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.INTERNAL_ERROR.getMessage())
                .jsonPath("$.errors[0].message").isEqualTo(TechnicalMessage.INTERNAL_ERROR.getMessage());
    }

    @Test
    void openCircuitReturnsServiceUnavailable() {
        when(franchiseServicePort.getFranchiseBranchProduct(1L)).thenReturn(Mono.error(
                CallNotPermittedException.createCallNotPermittedException(CircuitBreaker.ofDefaults("persistence"))));

        webTestClient.get().uri("/api/v1/franchise/{franchiseId}/top-stock-products", 1)
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.SERVICE_UNAVAILABLE.getCode());
    }
}
