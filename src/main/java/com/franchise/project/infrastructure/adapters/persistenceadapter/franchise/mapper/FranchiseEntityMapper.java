package com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.mapper;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.infrastructure.adapters.persistenceadapter.franchise.entity.FranchiseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {
    Franchise toModel(FranchiseEntity franchiseEntity);
    FranchiseEntity toEntity(Franchise franchise);
}
