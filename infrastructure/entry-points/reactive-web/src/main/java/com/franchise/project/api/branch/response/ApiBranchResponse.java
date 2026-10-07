package com.franchise.project.api.branch.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiBranchResponse(
        @Schema(example = "200") String code,
        @Schema(example = "Branch updated successfully") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        BranchResponse data
) {
}
