package com.franchise.project.r2dbc.branch.mapper;

import com.franchise.project.model.branch.Branch;
import com.franchise.project.r2dbc.branch.entity.BranchEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {
    Branch toModel(BranchEntity branchEntity);
    BranchEntity toEntity(Branch branch);
}
