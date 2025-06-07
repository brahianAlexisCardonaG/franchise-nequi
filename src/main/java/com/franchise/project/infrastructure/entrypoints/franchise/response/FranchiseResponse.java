package com.franchise.project.infrastructure.entrypoints.franchise.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseResponse {
    private Long id;
    private String Name;
}
