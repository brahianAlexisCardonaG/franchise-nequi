package com.franchise.project.model.franchise;

import com.franchise.project.model.branch.BranchProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseBranchProductList {
    private Long id;
    private String name;
    private List<BranchProduct> branches;
}
