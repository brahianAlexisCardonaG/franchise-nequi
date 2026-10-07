package com.franchise.project.api.branch.mapper;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.model.branch.BranchFranchise;
import com.franchise.project.api.branch.response.BranchFranchiseResponse;
import com.franchise.project.api.branch.response.BranchResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapperResponse {
    BranchFranchiseResponse toBranchFranchiseResponse(BranchFranchise branchFranchise);

    BranchResponse toBranchResponse(Branch branch);
}
