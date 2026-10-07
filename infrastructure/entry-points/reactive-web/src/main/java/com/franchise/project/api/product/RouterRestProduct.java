package com.franchise.project.api.product;

import com.franchise.project.api.product.dto.ProductDto;
import com.franchise.project.api.product.dto.ProductDtoUpdateName;
import com.franchise.project.api.product.dto.ProductDtoUpdateStock;
import com.franchise.project.api.product.handler.ProductHandler;
import com.franchise.project.api.product.response.ApiProductBranchResponse;
import com.franchise.project.api.product.response.ApiProductResponse;
import com.franchise.project.api.util.error.ApiErrorResponse;
import com.franchise.project.api.util.openapi.OpenApiExamples;
import com.franchise.project.api.util.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.franchise.project.api.util.Constants.PATH_PRODUCT;
import static com.franchise.project.api.util.Constants.PATH_PRODUCT_BY_ID;
import static com.franchise.project.api.util.Constants.PATH_PRODUCT_UPDATE_NAME;
import static com.franchise.project.api.util.Constants.PATH_PRODUCT_UPDATE_STOCK;
import static com.franchise.project.api.util.Constants.PRODUCT_ID_PATH_VARIABLE;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class RouterRestProduct {

    private static final String TAG = "Product";
    private static final String JSON = "application/json";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_PRODUCT,
                    method = RequestMethod.POST,
                    produces = JSON,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Add a product to a branch",
                            description = "Creates a product inside an existing branch. The name must be unique "
                                    + "within that branch and the stock must be greater than or equal to zero.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created",
                                            content = @Content(schema = @Schema(implementation = ApiProductBranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing fields, negative stock or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = {
                                                            @ExampleObject(name = "Invalid parameters", value = OpenApiExamples.INVALID_PARAMETERS),
                                                            @ExampleObject(name = "Negative stock", value = OpenApiExamples.PRODUCT_STOCK_INVALID),
                                                            @ExampleObject(name = "Malformed request", value = OpenApiExamples.MALFORMED_REQUEST)})),
                                    @ApiResponse(responseCode = "404", description = "The branch does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.BRANCH_NOT_FOUND))),
                                    @ApiResponse(responseCode = "409", description = "The branch already has a product with that name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.PRODUCT_ALREADY_EXISTS))),
                                    @ApiResponse(responseCode = "500", description = "Unexpected error",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INTERNAL_ERROR))),
                                    @ApiResponse(responseCode = "503", description = "Database unavailable or circuit breaker open",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.SERVICE_UNAVAILABLE)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_PRODUCT_BY_ID,
                    method = RequestMethod.DELETE,
                    produces = JSON,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProductBranch",
                    operation = @Operation(
                            operationId = "deleteProductBranch",
                            summary = "Delete a product from its branch",
                            description = "Permanently deletes the product.",
                            tags = TAG,
                            parameters = @Parameter(in = ParameterIn.PATH, name = PRODUCT_ID_PATH_VARIABLE,
                                    description = "Product identifier", required = true, example = "1",
                                    schema = @Schema(type = "integer", format = "int64")),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product deleted",
                                            content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
                                    @ApiResponse(responseCode = "400", description = "The product identifier is not numeric",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INVALID_PARAMETERS))),
                                    @ApiResponse(responseCode = "404", description = "The product does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.PRODUCT_NOT_FOUND))),
                                    @ApiResponse(responseCode = "500", description = "Unexpected error",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INTERNAL_ERROR))),
                                    @ApiResponse(responseCode = "503", description = "Database unavailable or circuit breaker open",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.SERVICE_UNAVAILABLE)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_PRODUCT_UPDATE_STOCK,
                    method = RequestMethod.PUT,
                    produces = JSON,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update the stock of a product",
                            description = "Replaces the stock of an existing product. The stock must be greater than or equal to zero.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductDtoUpdateStock.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated",
                                            content = @Content(schema = @Schema(implementation = ApiProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing fields, negative stock or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = {
                                                            @ExampleObject(name = "Invalid parameters", value = OpenApiExamples.INVALID_PARAMETERS),
                                                            @ExampleObject(name = "Negative stock", value = OpenApiExamples.PRODUCT_STOCK_INVALID)})),
                                    @ApiResponse(responseCode = "404", description = "The product does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.PRODUCT_NOT_FOUND))),
                                    @ApiResponse(responseCode = "500", description = "Unexpected error",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INTERNAL_ERROR))),
                                    @ApiResponse(responseCode = "503", description = "Database unavailable or circuit breaker open",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.SERVICE_UNAVAILABLE)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PATH_PRODUCT_UPDATE_NAME,
                    method = RequestMethod.PUT,
                    produces = JSON,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update the name of a product",
                            description = "Renames an existing product. The new name must be unique within its branch.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = ProductDtoUpdateName.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product renamed",
                                            content = @Content(schema = @Schema(implementation = ApiProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing identifier or name, or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INVALID_PARAMETERS))),
                                    @ApiResponse(responseCode = "404", description = "The product does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.PRODUCT_NOT_FOUND))),
                                    @ApiResponse(responseCode = "409", description = "The branch already has a product with that name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.PRODUCT_ALREADY_EXISTS))),
                                    @ApiResponse(responseCode = "500", description = "Unexpected error",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INTERNAL_ERROR))),
                                    @ApiResponse(responseCode = "503", description = "Database unavailable or circuit breaker open",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.SERVICE_UNAVAILABLE)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunctionProduct(ProductHandler productHandler) {
        return RouterFunctions
                .route(POST(PATH_PRODUCT), productHandler::createProduct)
                .andRoute(DELETE(PATH_PRODUCT_BY_ID), productHandler::deleteProductBranch)
                .andRoute(PUT(PATH_PRODUCT_UPDATE_STOCK), productHandler::updateProductStock)
                .andRoute(PUT(PATH_PRODUCT_UPDATE_NAME), productHandler::updateProductName);
    }
}
