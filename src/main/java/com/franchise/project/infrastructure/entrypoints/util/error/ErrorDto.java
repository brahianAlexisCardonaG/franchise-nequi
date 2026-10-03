package com.franchise.project.infrastructure.entrypoints.util.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ErrorDto(
        @Schema(example = "404") String code,
        @Schema(example = "The franchise does not exist") String message
) {
}
