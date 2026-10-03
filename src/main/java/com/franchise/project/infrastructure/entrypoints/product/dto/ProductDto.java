package com.franchise.project.infrastructure.entrypoints.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDto(
        @Schema(description = "Product name, unique within its branch", example = "Espresso")
        @NotBlank String name,
        @Schema(description = "Available units, must be greater than or equal to zero", example = "25")
        @NotNull Integer stock,
        @Schema(description = "Identifier of the branch that sells the product", example = "1")
        @NotNull Long branchId
) {
}
