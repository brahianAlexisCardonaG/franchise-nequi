package com.franchise.project.infrastructure.entrypoints.product.response;

import com.franchise.project.infrastructure.entrypoints.branch.response.BranchFranchiseResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiProductBranchResponse {
    private String code;
    private String message;
    private String date;
    private ProductBranchResponse data;
}
