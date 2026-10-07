package com.franchise.project.r2dbc.franchise.mapper;

import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.r2dbc.franchise.entity.FranchiseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {
    Franchise toModel(FranchiseEntity franchiseEntity);
    FranchiseEntity toEntity(Franchise franchise);
}
