package com.franchise.project.infrastructure.entrypoints.branch.mapper;

import com.franchise.project.domain.branch.BranchFranchise;
import com.franchise.project.infrastructure.entrypoints.branch.response.BranchFranchiseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchFranchiseMapperResponse {
    BranchFranchiseResponse toBranchFranchiseResponse(BranchFranchise branchFranchise);
}
