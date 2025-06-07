package com.franchise.project.infrastructure.adapters.persistenceadapter.branch.mapper;

import com.franchise.project.domain.branch.model.Branch;
import com.franchise.project.infrastructure.adapters.persistenceadapter.branch.entity.BranchEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {
    Branch toModel(BranchEntity branchEntity);
    BranchEntity toEntity(Branch branch);
}
