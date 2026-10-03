package com.franchise.project.infrastructure.entrypoints.franchise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record FranchiseDto(
        @Schema(description = "Franchise name, unique in the system", example = "Coffee House")
        @NotBlank String name
) {
}
