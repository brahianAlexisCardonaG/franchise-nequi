package com.franchise.project.infrastructure.entrypoints.franchise.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiFranchiseBranchProductResponse {
    private String code;
    private String message;
    private String date;
    private FranchiseBranchProductListResponse data;
}
