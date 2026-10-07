package com.franchise.project.api.franchise.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FranchiseResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Coffee House") String name
) {
}
