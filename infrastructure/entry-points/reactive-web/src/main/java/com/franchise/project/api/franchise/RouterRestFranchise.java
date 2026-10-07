package com.franchise.project.api.franchise;

import com.franchise.project.api.franchise.dto.FranchiseDto;
import com.franchise.project.api.franchise.dto.FranchiseDtoUpdateName;
import com.franchise.project.api.franchise.handler.FranchiseHandler;
import com.franchise.project.api.franchise.response.ApiFranchiseBranchProductResponse;
import com.franchise.project.api.franchise.response.ApiFranchiseResponse;
import com.franchise.project.api.util.error.ApiErrorResponse;
import com.franchise.project.api.util.openapi.OpenApiExamples;
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

import static com.franchise.project.api.util.Constants.FRANCHISE_ID_PATH_VARIABLE;
import static com.franchise.project.api.util.Constants.PATH_FRANCHISE;
import static com.franchise.project.api.util.Constants.PATH_FRANCHISE_TOP_STOCK_PRODUCTS;
import static com.franchise.project.api.util.Constants.PATH_FRANCHISE_UPDATE_NAME;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class RouterRestFranchise {

    private static final String TAG = "Franchise";
    private static final String JSON = "application/json";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_FRANCHISE,
                    method = RequestMethod.POST,
                    produces = JSON,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a franchise",
                            description = "Creates a franchise without branches. The name must be unique in the system.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseDto.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(schema = @Schema(implementation = ApiFranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing name or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = {
                                                            @ExampleObject(name = "Invalid parameters", value = OpenApiExamples.INVALID_PARAMETERS),
                                                            @ExampleObject(name = "Malformed request", value = OpenApiExamples.MALFORMED_REQUEST)})),
                                    @ApiResponse(responseCode = "409", description = "A franchise with the same name already exists",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.FRANCHISE_ALREADY_EXISTS))),
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
                    path = PATH_FRANCHISE_TOP_STOCK_PRODUCTS,
                    method = RequestMethod.GET,
                    produces = JSON,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getFranchiseIdBranchesProducts",
                    operation = @Operation(
                            operationId = "getFranchiseIdBranchesProducts",
                            summary = "Get the product with the largest stock of each branch of a franchise",
                            description = "Returns every branch of the franchise with its largest stock product. "
                                    + "Branches without products are returned with a null product.",
                            tags = TAG,
                            parameters = @Parameter(in = ParameterIn.PATH, name = FRANCHISE_ID_PATH_VARIABLE,
                                    description = "Franchise identifier", required = true, example = "1",
                                    schema = @Schema(type = "integer", format = "int64")),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Largest stock product per branch",
                                            content = @Content(schema = @Schema(implementation = ApiFranchiseBranchProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "The franchise identifier is not numeric",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INVALID_PARAMETERS))),
                                    @ApiResponse(responseCode = "404", description = "The franchise does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.FRANCHISE_NOT_FOUND))),
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
                    path = PATH_FRANCHISE_UPDATE_NAME,
                    method = RequestMethod.PUT,
                    produces = JSON,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update the name of a franchise",
                            description = "Renames an existing franchise. The new name must be unique in the system.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseDtoUpdateName.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise renamed",
                                            content = @Content(schema = @Schema(implementation = ApiFranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing identifier or name, or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INVALID_PARAMETERS))),
                                    @ApiResponse(responseCode = "404", description = "The franchise does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.FRANCHISE_NOT_FOUND))),
                                    @ApiResponse(responseCode = "409", description = "Another franchise already uses the name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.FRANCHISE_ALREADY_EXISTS))),
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
    public RouterFunction<ServerResponse> routerFunctionFranchise(FranchiseHandler franchiseHandler) {
        return RouterFunctions
                .route(POST(PATH_FRANCHISE), franchiseHandler::createFranchise)
                .andRoute(GET(PATH_FRANCHISE_TOP_STOCK_PRODUCTS), franchiseHandler::getFranchiseIdBranchesProducts)
                .andRoute(PUT(PATH_FRANCHISE_UPDATE_NAME), franchiseHandler::updateFranchiseName);
    }
}
