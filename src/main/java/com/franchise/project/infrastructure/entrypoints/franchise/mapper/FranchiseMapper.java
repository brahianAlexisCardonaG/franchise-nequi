package com.franchise.project.infrastructure.entrypoints.franchise.mapper;

import com.franchise.project.domain.franchise.model.Franchise;
import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    @Mapping(target="id", ignore = true)
    Franchise toFranchise(FranchiseDto franchiseDto);
}
