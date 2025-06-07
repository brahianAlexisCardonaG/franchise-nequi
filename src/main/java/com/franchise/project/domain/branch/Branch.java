package com.franchise.project.domain.branch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;
}
