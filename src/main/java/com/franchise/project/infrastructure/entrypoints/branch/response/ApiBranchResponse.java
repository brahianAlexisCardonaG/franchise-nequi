package com.franchise.project.infrastructure.entrypoints.branch.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiBranchResponse {
    private String code;
    private String message;
    private String date;
    private BranchResponse data;
}
