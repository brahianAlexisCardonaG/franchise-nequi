package com.franchise.project.infrastructure.entrypoints.branch.handler;

import com.franchise.project.domain.branch.api.BranchServicePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchFranchiseMapperResponse;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.branch.validations.BranchValidationDto;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.FRANCHISE_ERROR;
import static com.franchise.project.infrastructure.entrypoints.util.Constants.X_MESSAGE_ID;

@Component
@RequiredArgsConstructor
@Log4j2
public class BranchHandlerImpl {
    private final BranchValidationDto branchValidationDto;
    private final BranchMapper branchMapper;
    private final BranchFranchiseMapperResponse branchMapperResponse;
    private final BranchServicePort branchServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(BranchDto.class)
                .flatMap(branchValidationDto::validateFieldNotNullOrBlank)
                .map(branchMapper::toBranch)
                .flatMap(branch -> branchServicePort.createBranch(Mono.just(branch)))
                .map(branchMapperResponse::toBranchFranchiseResponse)
                .flatMap( franchise ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ApiBranchFranchiseResponse.builder()
                                        .code(TechnicalMessage.FRANCHISE_CREATED.getCode())
                                        .message(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                                        .date(Instant.now().toString())
                                        .data(franchise)
                                        .build())
                )
                .contextWrite(Context.of(X_MESSAGE_ID, ""))
                .doOnError(ex -> log.error(FRANCHISE_ERROR, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
