package com.franchise.project.domain.product.model;

import com.franchise.project.domain.branch.model.Branch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBranch {
    private Long id;
    private String name;
    private BigInteger stock;
    private Branch branch;
}
