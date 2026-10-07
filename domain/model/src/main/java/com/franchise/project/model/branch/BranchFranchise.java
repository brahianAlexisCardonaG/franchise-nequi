package com.franchise.project.model.branch;

import com.franchise.project.model.franchise.Franchise;
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
