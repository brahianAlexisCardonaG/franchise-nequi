package com.franchise.project.domain.franchise.model;

import com.franchise.project.domain.branch.model.BranchProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseBranchProductList {
    private Long id;
    private String name;
    private List<BranchProduct> branches;
}
