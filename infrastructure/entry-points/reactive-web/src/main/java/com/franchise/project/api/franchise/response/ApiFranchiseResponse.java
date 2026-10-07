package com.franchise.project.api.franchise.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiFranchiseResponse(
        @Schema(example = "201") String code,
        @Schema(example = "Franchise created successfully") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        FranchiseResponse data
) {
}
