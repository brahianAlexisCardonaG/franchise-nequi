package com.franchise.project.infrastructure.entrypoints.branch.mapper;

import com.franchise.project.domain.branch.Branch;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    Branch toBranch(BranchDto branchDto);
    BranchDto toBranchDto(Branch branch);
}
