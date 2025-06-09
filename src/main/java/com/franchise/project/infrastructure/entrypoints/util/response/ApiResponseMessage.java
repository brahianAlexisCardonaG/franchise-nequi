package com.franchise.project.infrastructure.entrypoints.util.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseMessage {
    private String code;
    private String message;
    private String date;
}
