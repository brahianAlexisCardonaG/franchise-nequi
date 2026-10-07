package com.franchise.project.api.branch.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiBranchFranchiseResponse(
        @Schema(example = "201") String code,
        @Schema(example = "Branch created successfully") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        BranchFranchiseResponse data
) {
}
