package com.franchise.project.api.franchise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FranchiseDtoUpdateName(
        @Schema(description = "Franchise identifier", example = "1")
        @NotNull Long id,
        @Schema(description = "New franchise name", example = "Coffee House Express")
        @NotBlank String name
) {
}
