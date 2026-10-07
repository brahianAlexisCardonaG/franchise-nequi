package com.franchise.project.api.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDtoUpdateName(
        @Schema(description = "Product identifier", example = "1")
        @NotNull Long id,
        @Schema(description = "New product name", example = "Double Espresso")
        @NotBlank String name
) {
}
