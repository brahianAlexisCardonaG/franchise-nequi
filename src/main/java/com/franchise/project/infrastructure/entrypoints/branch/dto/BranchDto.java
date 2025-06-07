package com.franchise.project.infrastructure.entrypoints.branch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDto {
    private String name;
    private Long franchiseId;
}
