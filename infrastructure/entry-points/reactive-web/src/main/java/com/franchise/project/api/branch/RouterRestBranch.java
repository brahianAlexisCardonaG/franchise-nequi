package com.franchise.project.api.branch;

import com.franchise.project.api.branch.dto.BranchDto;
import com.franchise.project.api.branch.dto.BranchDtoUpdateName;
import com.franchise.project.api.branch.handler.BranchHandler;
import com.franchise.project.api.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.api.branch.response.ApiBranchResponse;
import com.franchise.project.api.util.error.ApiErrorResponse;
import com.franchise.project.api.util.openapi.OpenApiExamples;
import io.swagger.v3.oas.annotations.Operation;
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

import static com.franchise.project.api.util.Constants.PATH_BRANCH_UPDATE_NAME;
import static com.franchise.project.api.util.Constants.PATH_POST_BRANCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class RouterRestBranch {

    private static final String TAG = "Branch";
    private static final String JSON = "application/json";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_POST_BRANCH,
                    method = RequestMethod.POST,
                    produces = JSON,
                    beanClass = BranchHandler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Add a branch to a franchise",
                            description = "Creates a branch inside an existing franchise. "
                                    + "The name must be unique within that franchise.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchDto.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created",
                                            content = @Content(schema = @Schema(implementation = ApiBranchFranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing name or franchise identifier, or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = {
                                                            @ExampleObject(name = "Invalid parameters", value = OpenApiExamples.INVALID_PARAMETERS),
                                                            @ExampleObject(name = "Malformed request", value = OpenApiExamples.MALFORMED_REQUEST)})),
                                    @ApiResponse(responseCode = "404", description = "The franchise does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.FRANCHISE_NOT_FOUND))),
                                    @ApiResponse(responseCode = "409", description = "The franchise already has a branch with that name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.BRANCH_ALREADY_EXISTS))),
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
                    path = PATH_BRANCH_UPDATE_NAME,
                    method = RequestMethod.PUT,
                    produces = JSON,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update the name of a branch",
                            description = "Renames an existing branch. The new name must be unique within its franchise.",
                            tags = TAG,
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = BranchDtoUpdateName.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch renamed",
                                            content = @Content(schema = @Schema(implementation = ApiBranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Missing identifier or name, or malformed body",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.INVALID_PARAMETERS))),
                                    @ApiResponse(responseCode = "404", description = "The branch does not exist",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.BRANCH_NOT_FOUND))),
                                    @ApiResponse(responseCode = "409", description = "The franchise already has a branch with that name",
                                            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class),
                                                    examples = @ExampleObject(value = OpenApiExamples.BRANCH_ALREADY_EXISTS))),
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
    public RouterFunction<ServerResponse> routerFunctionBranch(BranchHandler branchHandler) {
        return RouterFunctions
                .route(POST(PATH_POST_BRANCH), branchHandler::createBranch)
                .andRoute(PUT(PATH_BRANCH_UPDATE_NAME), branchHandler::updateBranchName);
    }
}
