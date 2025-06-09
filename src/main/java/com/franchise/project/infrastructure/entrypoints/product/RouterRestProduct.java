package com.franchise.project.infrastructure.entrypoints.product;

import com.franchise.project.infrastructure.entrypoints.product.dto.ProductDto;
import com.franchise.project.infrastructure.entrypoints.product.handler.ProductHandlerImpl;
import com.franchise.project.infrastructure.entrypoints.product.response.ApiProductBranchResponse;
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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.PATH_PRODUCT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
@Tag(name = "Product", description = "API Product")
public class RouterRestProduct {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_PRODUCT,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = ProductHandlerImpl.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create Product",
                            tags = {"Endpoints Product"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Product create successfully",
                                            content = @Content(schema = @Schema(implementation
                                                    = ApiProductBranchResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validation"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_PRODUCT,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.DELETE,
                    beanClass = ProductHandlerImpl.class,
                    beanMethod = "deleteProductBranch",
                    operation = @Operation(
                            operationId = "deleteProductBranch",
                            summary = "Delete Product relate with Branch",
                            tags = { "Endpoints Product" },
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            in = ParameterIn.QUERY,
                                            name = "productId",
                                            description = "productId",
                                            example = "1",
                                            required = true
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Product relate with Branch deleted successfully",
                                            content = @Content(schema = @Schema(implementation
                                                    = ApiResponseMessage.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionProduct(ProductHandlerImpl productHandler) {
        return RouterFunctions
                .route(POST(PATH_PRODUCT), productHandler::createProduct).
                andRoute(DELETE(PATH_PRODUCT), productHandler::deleteProductBranch);
    }
}
