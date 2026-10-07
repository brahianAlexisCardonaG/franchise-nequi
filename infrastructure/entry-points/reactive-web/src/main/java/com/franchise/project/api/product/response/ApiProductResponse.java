package com.franchise.project.api.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ApiProductResponse(
        @Schema(example = "200") String code,
        @Schema(example = "Product updated successfully") String message,
        @Schema(example = "2026-01-01T12:00:00Z") String date,
        ProductResponse data
) {
}
