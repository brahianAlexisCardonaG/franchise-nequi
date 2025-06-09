package com.franchise.project.infrastructure.entrypoints.franchise.response;

import com.franchise.project.infrastructure.entrypoints.branch.response.BranchProductResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FranchiseBranchProductListResponse {
    private Long id;
    private String name;
    private List<BranchProductResponse> branches;
}
