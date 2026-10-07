package com.franchise.project.api.branch.handler;

import com.franchise.project.usecase.createbranch.CreateBranchUseCase;
import com.franchise.project.usecase.updatebranchname.UpdateBranchNameUseCase;
import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchFranchise;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.model.exception.BusinessException;
import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.api.branch.RouterRestBranch;
import com.franchise.project.api.branch.mapper.BranchMapperImpl;
import com.franchise.project.api.branch.mapper.BranchMapperResponseImpl;
import com.franchise.project.api.util.validation.RequestValidator;
import jakarta.validation.Validation;
import com.franchise.project.api.util.error.ApplyErrorHandler;
import com.franchise.project.api.util.error.BuildErrorResponse;
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
class BranchHandlerTest {

    @Mock
    private CreateBranchUseCase createBranchUseCase;
    @Mock
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        BranchHandler handler = new BranchHandler(new RequestValidator(Validation.buildDefaultValidatorFactory().getValidator()), new BranchMapperImpl(),
                new BranchMapperResponseImpl(), createBranchUseCase, updateBranchNameUseCase, new ApplyErrorHandler(new BuildErrorResponse()));
        webTestClient = WebTestClient.bindToRouterFunction(new RouterRestBranch().routerFunctionBranch(handler)).build();
    }

    @Test
    void createBranchReturnsCreated() {
        when(createBranchUseCase.createBranch(new Branch(null, "Downtown", 1L)))
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
        verifyNoInteractions(createBranchUseCase, updateBranchNameUseCase);
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
        verifyNoInteractions(createBranchUseCase, updateBranchNameUseCase);
    }

    @Test
    void createBranchForUnknownFranchiseReturnsNotFound() {
        when(createBranchUseCase.createBranch(new Branch(null, "Downtown", 99L)))
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
        when(updateBranchNameUseCase.updateBranchName(new Branch(5L, "Uptown", null)))
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
        when(updateBranchNameUseCase.updateBranchName(new Branch(5L, "Taken", null)))
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
