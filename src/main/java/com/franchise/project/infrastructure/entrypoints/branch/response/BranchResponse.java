package com.franchise.project.infrastructure.entrypoints.branch.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BranchResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Downtown") String name
) {
}
