package com.franchise.project.api.branch.response;

import com.franchise.project.api.franchise.response.FranchiseResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record BranchFranchiseResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Downtown") String name,
        FranchiseResponse franchise
) {
}
