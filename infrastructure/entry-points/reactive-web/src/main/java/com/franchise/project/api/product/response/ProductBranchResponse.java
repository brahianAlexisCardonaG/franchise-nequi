package com.franchise.project.api.product.response;

import com.franchise.project.api.branch.response.BranchResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductBranchResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Espresso") String name,
        @Schema(example = "25") Integer stock,
        BranchResponse branch
) {
}
