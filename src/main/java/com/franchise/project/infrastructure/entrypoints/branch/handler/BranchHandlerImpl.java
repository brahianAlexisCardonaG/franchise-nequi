package com.franchise.project.infrastructure.entrypoints.branch.handler;

import com.franchise.project.domain.branch.api.CreateBranchServicePort;
import com.franchise.project.domain.branch.api.UpdateBranchNameServicePort;
import com.franchise.project.domain.enums.TechnicalMessage;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDtoUpdateName;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.franchise.project.infrastructure.entrypoints.branch.mapper.BranchMapperResponse;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.branch.response.ApiBranchResponse;
import com.franchise.project.infrastructure.entrypoints.util.error.ApplyErrorHandler;
import com.franchise.project.infrastructure.entrypoints.util.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.franchise.project.infrastructure.entrypoints.util.Constants.REQUEST_FAILED_LOG;

@Component
@RequiredArgsConstructor
@Slf4j
public class BranchHandlerImpl {
    private final RequestValidator requestValidator;
    private final BranchMapper branchMapper;
    private final BranchMapperResponse branchMapperResponse;
    private final CreateBranchServicePort createBranchServicePort;
    private final UpdateBranchNameServicePort updateBranchNameServicePort;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(BranchDto.class)
                .flatMap(requestValidator::validate)
                .map(branchMapper::toBranch)
                .flatMap(createBranchServicePort::createBranch)
                .map(branchMapperResponse::toBranchFranchiseResponse)
                .flatMap(branch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiBranchFranchiseResponse.builder()
                                .code(TechnicalMessage.BRANCH_CREATED.getCode())
                                .message(TechnicalMessage.BRANCH_CREATED.getMessage())
                                .date(Instant.now().toString())
                                .data(branch)
                                .build()))
                .doOnError(ex -> log.error(REQUEST_FAILED_LOG, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(BranchDtoUpdateName.class)
                .flatMap(requestValidator::validate)
                .map(branchMapper::toBranchUpdateName)
                .flatMap(updateBranchNameServicePort::updateBranchName)
                .map(branchMapperResponse::toBranchResponse)
                .flatMap(branch -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiBranchResponse.builder()
                                .code(TechnicalMessage.BRANCH_UPDATE.getCode())
                                .message(TechnicalMessage.BRANCH_UPDATE.getMessage())
                                .date(Instant.now().toString())
                                .data(branch)
                                .build()))
                .doOnError(ex -> log.error(REQUEST_FAILED_LOG, ex));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
