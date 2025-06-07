package com.franchise.project.infrastructure.entrypoints.branch;

import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.handler.BranchHandlerImpl;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import com.franchise.project.infrastructure.entrypoints.franchise.handler.FranchiseHandlerImpl;
import com.franchise.project.infrastructure.entrypoints.franchise.response.ApiFranchiseResponse;
import io.swagger.v3.oas.annotations.Operation;
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

import static com.franchise.project.infrastructure.entrypoints.util.Constants.PATH_POST_BRANCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
@Tag(name = "Branch", description = "API Branch")
public class RouterRestBranch {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PATH_POST_BRANCH,
                    produces = {"application/json"},
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = BranchHandlerImpl.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Create Branch",
                            tags = {"Endpoints Branch"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchDto.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Branch create successfully",
                                            content = @Content(schema = @Schema(implementation
                                                    = ApiBranchFranchiseResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validation"
                                    )
                            }
                    )
            ),
    })
    public RouterFunction<ServerResponse> routerFunctionBranch(BranchHandlerImpl branchHandler) {
        return RouterFunctions
                .route(POST(PATH_POST_BRANCH), branchHandler::createBranch);
    }
}
