package com.franchise.project.infrastructure.entrypoints.branch.handler;

import com.franchise.project.domain.branch.api.BranchServicePort;
import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapperResponse;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchResponse;
import com.franchise.project.infrastructure.entrypoints.branch.validations.BranchValidationDto;
import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.util.Constants;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchHandlerImplTest {

    @Mock
    private BranchValidationDto branchValidationDto;
    @Mock
    private BranchMapper branchMapper;
    @Mock
    private BranchMapperResponse branchMapperResponse;
    @Mock
    private BranchServicePort branchServicePort;
    @Mock
    private ApplyErrorHandler applyErrorHandler;

    private BranchHandlerImpl handler;
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        handler = new BranchHandlerImpl(
                branchValidationDto,
                branchMapper,
                branchMapperResponse,
                branchServicePort,
                applyErrorHandler
        );

        var router = RouterFunctions.route()
                .POST(Constants.PATH_POST_BRANCH, handler::createBranch)
                .PUT(Constants.PATH_BRANCH_UPDATE_NAME, handler::updateBranchName)
                .build();

        webClient = WebTestClient.bindToRouterFunction(router).build();
    }

    @Test
    void createBranch_shouldReturnCreatedResponse() {
        BranchDto dto = new BranchDto();
        dto.setName("Sucursal Norte");

        Branch branchDomain = new Branch(1L, "Sucursal Norte", 100L);

        Franchise franchise = new Franchise(10L, "Franquicia A");
        BranchFranchise branchFranchise = new BranchFranchise(1L, "Sucursal Norte", franchise);

        BranchFranchiseResponse responseMapped = new BranchFranchiseResponse();
        responseMapped.setId(1L);
        responseMapped.setName("Sucursal Norte");
        responseMapped.setFranchise(new FranchiseResponse(10L, "Franquicia A"));

        when(branchValidationDto.validateFieldNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(branchMapper.toBranch(dto)).thenReturn(branchDomain);
        when(branchServicePort.createBranch(any(Branch.class))).thenReturn(Mono.just(branchFranchise));
        when(branchMapperResponse.toBranchFranchiseResponse(branchFranchise)).thenReturn(responseMapped);
        when(applyErrorHandler.applyErrorHandling(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        webClient.post()
                .uri(Constants.PATH_POST_BRANCH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.BRANCH_CREATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.BRANCH_CREATED.getMessage())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Sucursal Norte")
                .jsonPath("$.data.franchise.id").isEqualTo(10)
                .jsonPath("$.data.franchise.name").isEqualTo("Franquicia A");
    }

    @Test
    void updateBranchName_shouldReturnOkResponse() {
        BranchDtoUpdateName dto = new BranchDtoUpdateName(1L, "Sucursal Centro");

        Branch branchDomain = new Branch(1L, "Sucursal Centro", 200L);
        BranchResponse branchResponse = new BranchResponse(1L, "Sucursal Centro");

        when(branchValidationDto.validateDtoBranchNameNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(branchMapper.toBranchUpdateName(dto)).thenReturn(branchDomain);
        when(branchServicePort.updateName(any(Branch.class))).thenReturn(Mono.just(branchDomain));
        when(branchMapperResponse.toBranchResponse(branchDomain)).thenReturn(branchResponse);
        when(applyErrorHandler.applyErrorHandling(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));

        webClient.put()
                .uri(Constants.PATH_BRANCH_UPDATE_NAME)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.BRANCH_UPDATE.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.BRANCH_UPDATE.getMessage())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Sucursal Centro");
    }
}
