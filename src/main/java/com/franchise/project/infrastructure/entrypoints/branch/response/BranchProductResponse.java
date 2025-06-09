package com.franchise.project.infrastructure.entrypoints.branch.response;

import com.franchise.project.infrastructure.entrypoints.product.response.ProductResponse;
import lombok.Data;

@Data
public class BranchProductResponse {
    private Long id;
    private String name;
    private ProductResponse product;
}