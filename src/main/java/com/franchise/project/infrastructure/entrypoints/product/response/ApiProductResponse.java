package com.franchise.project.infrastructure.entrypoints.product.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiProductResponse {
    private String code;
    private String message;
    private String date;
    private ProductResponse data;
}
