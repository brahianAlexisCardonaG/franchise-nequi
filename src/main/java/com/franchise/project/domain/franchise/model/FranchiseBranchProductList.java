package com.franchise.project.domain.franchise.model;

import com.franchise.project.domain.branch.model.BranchProduct;
import lombok.Data;

import java.util.List;

@Data
public class FranchiseBranchProductList {
    private Long id;
    private String name;
    private List<BranchProduct> branches;
}
