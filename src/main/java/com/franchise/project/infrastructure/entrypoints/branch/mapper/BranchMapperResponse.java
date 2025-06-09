package com.franchise.project.infrastructure.entrypoints.branch.mapper;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.domain.branch.model.BranchFranchise;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchFranchiseResponse;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapperResponse {
    BranchFranchiseResponse toBranchFranchiseResponse(BranchFranchise branchFranchise);

    BranchResponse toBranchResponse(Branch branch);
}
