package com.franchise.project.api.franchise.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiFranchiseBranchProductResponse(
        @Schema(example = "200") String code,
        @Schema(example = "Largest stock product per branch retrieved successfully") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        FranchiseBranchProductListResponse data
) {
}
