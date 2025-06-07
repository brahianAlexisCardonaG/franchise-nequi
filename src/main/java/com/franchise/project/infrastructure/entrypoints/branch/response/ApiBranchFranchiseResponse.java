package com.franchise.project.infrastructure.entrypoints.branch.response;

import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiBranchFranchiseResponse {
    private String code;
    private String message;
    private String date;
    private BranchFranchiseResponse data;
}
