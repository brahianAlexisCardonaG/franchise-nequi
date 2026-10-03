package com.franchise.project.infrastructure.entrypoints.branch.response;

import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record BranchFranchiseResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Downtown") String name,
        FranchiseResponse franchise
) {
}
