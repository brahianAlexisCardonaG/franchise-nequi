package com.franchise.project.infrastructure.entrypoints.util.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiErrorResponse {
    private String code;
    private String message;
    private String date;
    private List<ErrorDto> errors;
}
