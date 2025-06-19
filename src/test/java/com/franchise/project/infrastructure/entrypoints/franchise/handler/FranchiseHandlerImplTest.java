package com.franchise.project.infrastructure.entrypoints.franchise.handler;

import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.domain.franchise.api.FranchiseServicePort;
import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.domain.franchise.model.FranchiseBranchProductList;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapper;
import com.franchise.project.infrastructure.entrypoints.franchise.mapper.FranchiseMapperResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseBranchProductListResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.validations.FranchiseValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FranchiseHandlerImplTest {

    @Mock
    private FranchiseValidationDto franchiseValidationDto;
    @Mock
    private FranchiseMapper franchiseMapper;
    @Mock
    private FranchiseMapperResponse franchiseMapperResponse;
    @Mock
    private FranchiseServicePort franchiseServicePort;
    @Mock
    private ApplyErrorHandler applyErrorHandler;

    private FranchiseHandlerImpl handler;
    private WebTestClient webClient;

    @BeforeEach
    void setup() {
        handler = new FranchiseHandlerImpl(
                franchiseValidationDto,
                franchiseMapper,
                franchiseMapperResponse,
                franchiseServicePort,
                applyErrorHandler
        );


        var router = RouterFunctions
                .route(RequestPredicates.POST("/api/v1/franchise"), handler::createFranchise)
                .andRoute(RequestPredicates.GET("/api/v1/franchise").and(RequestPredicates.queryParam("franchiseId", v -> true)), handler::getFranchiseIdBranchesProducts)
                .andRoute(RequestPredicates.PUT("/api/v1/franchise/name"), handler::updateFranchiseName);

        webClient = WebTestClient.bindToRouterFunction(router).

                build();
    }

    @Test
    void createFranchise_shouldReturnCreatedResponse() {
        FranchiseDto dto = new FranchiseDto("Franquicia X");
        Franchise domain = new Franchise(1L, "Franquicia X");
        FranchiseResponse response = new FranchiseResponse(1L, "Franquicia X");

        when(franchiseValidationDto.validateFieldNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(franchiseMapper.toFranchise(dto)).thenReturn(domain);
        when(franchiseServicePort.createFranchise(any(Mono.class))).thenReturn(Mono.just(domain));
        when(franchiseMapperResponse.toFranchiseResponse(domain)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_CREATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Franquicia X");
    }


    @Test
    void getFranchiseIdBranchesProducts_shouldReturnOkResponse() {
        Long franchiseId = 1L;
        FranchiseBranchProductList domain = new FranchiseBranchProductList(); // puedes poblar si deseas
        FranchiseBranchProductListResponse response = new FranchiseBranchProductListResponse(); // igual

        when(franchiseServicePort.getFranchiseBranchProduct(franchiseId)).thenReturn(Mono.just(domain));
        when(franchiseMapperResponse.toFranchiseBranchProductListResponse(domain)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/franchise")
                        .queryParam("franchiseId", franchiseId.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.FRANCHISE_BRANCH_PRODUCT_FOUND.getMessage());
    }

    @Test
    void updateFranchiseName_shouldReturnCreatedResponse() {
        FranchiseDtoUpdateName dto = new FranchiseDtoUpdateName();
        dto.setId(1L);
        dto.setName("Franquicia Actualizada");

        Franchise domain = new Franchise(1L, "Franquicia Actualizada");
        FranchiseResponse response = new FranchiseResponse(1L, "Franquicia Actualizada");

        when(franchiseValidationDto.validateFranchiseDtoNameNotNullOrBlank(dto)).thenReturn(Mono.just(dto));
        when(franchiseMapper.toFranchiseUpdateName(dto)).thenReturn(domain);
        when(franchiseServicePort.updateName(any(Mono.class))).thenReturn(Mono.just(domain));
        when(franchiseMapperResponse.toFranchiseResponse(domain)).thenReturn(response);
        when(applyErrorHandler.applyErrorHandling(any())).thenAnswer(inv -> inv.getArgument(0));

        webClient.put()
                .uri("/api/v1/franchise/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.code").isEqualTo(TechnicalMessage.PRODUCT_UPDATE.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_UPDATE.getMessage())
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Franquicia Actualizada");
    }
}