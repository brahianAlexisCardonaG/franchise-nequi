package com.franchise.project.infrastructure.entrypoints.franchise.response;

import com.franchise.project.infrastructure.entrypoints.branch.response.BranchProductResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FranchiseBranchProductListResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Coffee House") String name,
        List<BranchProductResponse> branches
) {
}
