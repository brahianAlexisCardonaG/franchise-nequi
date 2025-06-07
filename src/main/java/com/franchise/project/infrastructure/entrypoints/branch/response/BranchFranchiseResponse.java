package com.franchise.project.infrastructure.entrypoints.branch.response;

import com.franchise.project.infrastructure.entrypoints.franchise.response.FranchiseResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BranchFranchiseResponse {
    private Long id;
    private String Name;
    private FranchiseResponse franchise;
}
