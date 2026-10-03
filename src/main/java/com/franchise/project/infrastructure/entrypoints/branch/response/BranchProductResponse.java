package com.franchise.project.infrastructure.entrypoints.branch.response;

import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;

public record BranchProductResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Downtown") String name,
        @Schema(description = "Largest stock product of the branch, null when the branch has no products")
        ProductResponse product
) {
}
