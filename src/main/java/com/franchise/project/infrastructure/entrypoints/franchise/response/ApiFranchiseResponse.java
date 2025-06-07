package com.franchise.project.infrastructure.entrypoints.franchise.response;

import com.franchise.project.infrastructure.entrypoints.franchise.dto.FranchiseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiFranchiseResponse {
    private String code;
    private String message;
    private String date;
    private FranchiseResponse data;
}
