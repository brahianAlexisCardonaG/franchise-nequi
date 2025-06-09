package com.franchise.project.infrastructure.entrypoints.branch.mapper;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDto;
import com.franchise.project.infrastructure.entrypoints.branch.dto.BranchDtoUpdateName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "id", ignore = true)
    Branch toBranch(BranchDto branchDto);

    @Mapping(target = "franchiseId", ignore = true)
    Branch toBranchUpdateName(BranchDtoUpdateName branchDtoUpdateName);
}
