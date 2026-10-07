package com.franchise.project.api.branch.mapper;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.api.branch.dto.BranchDto;
import com.franchise.project.api.branch.dto.BranchDtoUpdateName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "id", ignore = true)
    Branch toBranch(BranchDto branchDto);

    @Mapping(target = "franchiseId", ignore = true)
    Branch toBranchUpdateName(BranchDtoUpdateName branchDtoUpdateName);
}
