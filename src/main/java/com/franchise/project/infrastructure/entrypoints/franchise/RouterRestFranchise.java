package com.franchise.project.infrastructure.entrypoints.franchise;

import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseBranchProductResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.handler.FranchiseHandlerImpl;
import com.franchise.project.infrastructure.entrypoints.product.handler.ProductHandlerImpl;
import com.franchise.project.infrastructure.entrypoints.util.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@Tag(name = "Franchise", description = "API Franchise")
public class RouterRestFranchise {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_FRANCHISE,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create Franchise",
                            tags = {"Endpoints Franchise"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franchise create successfully",
                                            content = @Content(schema = @Schema(implementation = ApiFranchiseResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validation"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_FRANCHISE,
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandlerImpl.class,
                    beanMethod = "getFranchiseIdBranchesProducts",
                    operation = @Operation(
                            operationId = "getFranchiseIdBranchesProducts",
                            summary = "Get Product largest stock by branch. franchiseId",
                            tags = { "Endpoints Franchise" },
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "franchiseId",
                                            description = "franchiseId",
                                            example = "1",
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Product relate with Branch deleted successfully",
                                            content = @Content(schema = @Schema(implementation
                                                    = ApiFranchiseBranchProductResponse.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionFranchise(FranchiseHandlerImpl franchiseHandler) {
        return RouterFunctions
                .route(POST(PATH_FRANCHISE), franchiseHandler::createFranchise)
                .andRoute(GET(PATH_FRANCHISE), franchiseHandler::getFranchiseIdBranchesProducts)
                ;

    }
}
