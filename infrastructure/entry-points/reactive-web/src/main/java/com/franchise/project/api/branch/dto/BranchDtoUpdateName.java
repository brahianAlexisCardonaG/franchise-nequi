package com.franchise.project.api.branch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BranchDtoUpdateName(
        @Schema(description = "Branch identifier", example = "1")
        @NotNull Long id,
        @Schema(description = "New branch name", example = "Uptown")
        @NotBlank String name
) {
}
