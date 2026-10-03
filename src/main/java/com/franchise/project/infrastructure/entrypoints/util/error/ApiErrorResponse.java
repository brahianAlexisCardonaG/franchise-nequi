package com.franchise.project.infrastructure.entrypoints.util.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        @Schema(example = "404") String code,
        @Schema(example = "The franchise does not exist") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        List<ErrorDto> errors
) {
}
