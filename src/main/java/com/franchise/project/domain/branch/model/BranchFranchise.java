package com.franchise.project.domain.branch.model;

import com.franchise.project.domain.franchise.model.Franchise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchFranchise {
    private Long id;
    private String name;
    private Franchise franchise;
}
