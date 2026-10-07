package com.franchise.project.api.franchise.mapper;

import com.franchise.project.model.franchise.Franchise;
import com.franchise.project.api.franchise.dto.FranchiseDto;
import com.franchise.project.api.franchise.dto.FranchiseDtoUpdateName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {
    @Mapping(target="id", ignore = true)
    Franchise toFranchise(FranchiseDto franchiseDto);

    Franchise toFranchiseUpdateName(FranchiseDtoUpdateName franchiseDtoUpdateName);
}
