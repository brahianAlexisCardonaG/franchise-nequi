package com.franchise.project.infrastructure.entrypoints.product.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchFranchiseResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiProductBranchResponse {
    private String code;
    private String message;
    private String date;
    private ProductBranchResponse data;
}
