package com.franchise.project.api.util.error;

import com.franchise.project.model.enums.TechnicalMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Component
public class BuildErrorResponse {

    public Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, TechnicalMessage error) {
        return Mono.defer(() -> ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiErrorResponse.builder()
                        .code(error.getCode())
                        .message(error.getMessage())
                        .date(Instant.now().toString())
                        .errors(List.of(ErrorDto.builder()
                                .code(error.getCode())
                                .message(error.getMessage())
                                .build()))
                        .build()));
    }
}
