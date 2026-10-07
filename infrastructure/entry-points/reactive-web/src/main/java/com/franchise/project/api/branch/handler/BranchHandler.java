package com.franchise.project.api.branch.handler;

import com.franchise.project.usecase.createbranch.CreateBranchUseCase;
import com.franchise.project.usecase.updatebranchname.UpdateBranchNameUseCase;
import com.franchise.project.model.enums.TechnicalMessage;
import com.franchise.project.api.branch.dto.BranchDto;
import com.franchise.project.api.branch.dto.BranchDtoUpdateName;
import com.franchise.project.api.branch.mapper.BranchMapper;
import com.franchise.project.api.branch.mapper.BranchMapperResponse;
import com.franchise.project.api.branch.response.ApiBranchFranchiseResponse;
import com.franchise.project.api.branch.response.ApiBranchResponse;
import com.franchise.project.api.util.error.ApplyErrorHandler;
import com.franchise.project.api.util.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;


@Component
@RequiredArgsConstructor
public class BranchHandler {
    private final RequestValidator requestValidator;
    private final BranchMapper branchMapper;
    private final BranchMapperResponse branchMapperResponse;
    private final CreateBranchUseCase createBranchUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final ApplyErrorHandler applyErrorHandler;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(BranchDto.class)
                .flatMap(requestValidator::validate)
                .map(branchMapper::toBranch)
                .flatMap(createBranchUseCase::createBranch)
                .map(branchMapperResponse::toBranchFranchiseResponse)
                .flatMap(branch -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiBranchFranchiseResponse.builder()
                                .code(TechnicalMessage.BRANCH_CREATED.getCode())
                                .message(TechnicalMessage.BRANCH_CREATED.getMessage())
                                .date(Instant.now().toString())
                                .data(branch)
                                .build()));
        return applyErrorHandler.applyErrorHandling(response);
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        Mono<ServerResponse> response = request.bodyToMono(BranchDtoUpdateName.class)
                .flatMap(requestValidator::validate)
                .map(branchMapper::toBranchUpdateName)
                .flatMap(updateBranchNameUseCase::updateBranchName)
                .map(branchMapperResponse::toBranchResponse)
                .flatMap(branch -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiBranchResponse.builder()
                                .code(TechnicalMessage.BRANCH_UPDATE.getCode())
                                .message(TechnicalMessage.BRANCH_UPDATE.getMessage())
                                .date(Instant.now().toString())
                                .data(branch)
                                .build()));
        return applyErrorHandler.applyErrorHandling(response);
    }
}
