package com.franchise.project.domain.branch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private Long id;
    private String name;
    private Long franchiseId;
}
