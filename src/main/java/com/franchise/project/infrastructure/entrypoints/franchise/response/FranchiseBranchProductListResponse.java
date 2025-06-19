package com.franchise.project.infrastructure.entrypoints.franchise.response;

import com.franchise.project.infrastructure.entrypoints.branch.response.BranchProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseBranchProductListResponse {
    private Long id;
    private String name;
    private List<BranchProductResponse> branches;
}
