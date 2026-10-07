package com.franchise.project.api.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductDtoUpdateStock(
        @Schema(description = "Product identifier", example = "1")
        @NotNull Long id,
        @Schema(description = "New available units, must be greater than or equal to zero", example = "40")
        @NotNull Integer stock
) {
}
