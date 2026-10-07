package com.franchise.project.api.branch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BranchDto(
        @Schema(description = "Branch name, unique within its franchise", example = "Downtown")
        @NotBlank String name,
        @Schema(description = "Identifier of the franchise that owns the branch", example = "1")
        @NotNull Long franchiseId
) {
}
