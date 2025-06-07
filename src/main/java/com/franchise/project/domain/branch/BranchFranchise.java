package com.franchise.project.domain.branch;

import com.franchise.project.domain.franchise.model.Franchise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchFranchise {
    private Long id;
    private String Name;
    private Franchise franchise;
}
