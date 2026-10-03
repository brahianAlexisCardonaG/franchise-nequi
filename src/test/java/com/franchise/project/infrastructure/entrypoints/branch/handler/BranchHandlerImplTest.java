package com.franchise.project.infrastructure.entrypoints.branch.handler;

import com.franchise.project.domain.branch.api.CreateBranchServicePort;
import com.franchise.project.domain.branch.api.UpdateBranchNameServicePort;
import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.exception.BusinessException;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.infrastructure.entrypoints.branch.RouterRestBranch;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapperImpl;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapperResponseImpl;
import com.franchise.project.infrastructure.entrypoints.util.validation.RequestValidator;
import jakarta.validation.Validation;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.error.BuildErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerImplTest {

    @Mock
    private CreateBranchServicePort createBranchServicePort;
    @Mock
    private UpdateBranchNameServicePort updateBranchNameServicePort;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        BranchHandlerImpl handler = new BranchHandlerImpl(new RequestValidator(Validation.buildDefaultValidatorFactory().getValidator()), new BranchMapperImpl(),
                new BranchMapperResponseImpl(), createBranchServicePort, updateBranchNameServicePort, new ApplyErrorHandler(new BuildErrorResponse()));
        webTestClient = WebTestClient.bindToRouterFunction(new RouterRestBranch().routerFunctionBranch(handler)).build();
    }

    @Test
    void createBranchReturnsCreated() {
        when(createBranchServicePort.createBranch(new Branch(null, "Downtown", 1L)))
                .thenReturn(Mono.just(new BranchFranchise(100L, "Downtown", new Franchise(1L, "Franchise1"))));

        webTestClient.post().uri("/api/v1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Downtown\",\"franchiseId\":1}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.BRANCH_CREATED.getCode())
                .jsonPath("$.data.id").isEqualTo(100)
                .jsonPath("$.data.name").isEqualTo("Downtown")
                .jsonPath("$.data.franchise.id").isEqualTo(1);
    }

    @Test
    void createBranchWithoutNameReturnsBadRequest() {
        webTestClient.post().uri("/api/v1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"franchiseId\":1}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.INVALID_PARAMETERS.getMessage());
        verifyNoInteractions(createBranchServicePort, updateBranchNameServicePort);
    }

    @Test
    void createBranchWithMalformedJsonReturnsBadRequest() {
        webTestClient.post().uri("/api/v1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(TechnicalMessage.INVALID_REQUEST.getMessage());
        verifyNoInteractions(createBranchServicePort, updateBranchNameServicePort);
    }

    @Test
    void createBranchForUnknownFranchiseReturnsNotFound() {
        when(createBranchServicePort.createBranch(new Branch(null, "Downtown", 99L)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_EXISTS)));

        webTestClient.post().uri("/api/v1/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Downtown\",\"franchiseId\":99}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_NOT_EXISTS.getCode());
    }

    @Test
    void updateBranchNameReturnsOk() {
        when(updateBranchNameServicePort.updateBranchName(new Branch(5L, "Uptown", null)))
                .thenReturn(Mono.just(new Branch(5L, "Uptown", 1L)));

        webTestClient.put().uri("/api/v1/branch/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":5,\"name\":\"Uptown\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.BRANCH_UPDATE.getCode())
                .jsonPath("$.data.name").isEqualTo("Uptown");
    }

    @Test
    void updateBranchNameWithDuplicatedNameReturnsConflict() {
        when(updateBranchNameServicePort.updateBranchName(new Branch(5L, "Taken", null)))
                .thenReturn(Mono.error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS)));

        webTestClient.put().uri("/api/v1/branch/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"id\":5,\"name\":\"Taken\"}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.BRANCH_ALREADY_EXISTS.getCode());
    }
}
