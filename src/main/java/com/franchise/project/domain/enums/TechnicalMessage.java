package com.franchise.project.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    INTERNAL_ERROR("500","Something went wrong, please try again", ""),
    INTERNAL_ERROR_IN_ADAPTERS("PRC501","Something went wrong in adapters, please try again", ""),
    INVALID_REQUEST("400", "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(INVALID_REQUEST.getCode(), "Bad Parameters, please verify data", ""),
    RESOURCE_ALREADY_EXISTS("409", "The resource already exists.", ""),
    SERVICE_UNAVAILABLE("503", "Service temporarily unavailable, please try again later", ""),

    FRANCHISE_CREATED("201", "Franchise created successfully", ""),
    FRANCHISE_NOT_EXISTS("404"," The Franchise are not registered." ,"" ),
    FRANCHISE_ALREADY_EXISTS("409"," The Franchise already found registered." ,"" ),
    FRANCHISE_BRANCH_PRODUCT_FOUND("200", "Products associates a branches by franchiseId", ""),
    FRANCHISE_UPDATE("200", "Franchise updated successfully", ""),

    BRANCH_CREATED("201", "Branch created successfully", ""),
    BRANCH_NOT_EXISTS("404"," The Branch are not registered." ,"" ),
    BRANCH_ALREADY_EXISTS("409"," The Branch already found registered." ,"" ),
    BRANCH_UPDATE("200", "branch updated successfully", ""),

    PRODUCT_CREATED("201", "Product created successfully", ""),
    PRODUCT_NOT_EXISTS("404"," The Product are not registered." ,"" ),
    PRODUCT_ALREADY_EXISTS("409"," The Product already found registered." ,"" ),
    PRODUCT_STOCK_INVALID("400", "The product stock must be greater than or equal to zero.", ""),
    PRODUCT_BRANCH_DELETE("200","The Product was deleted successfully." ,"" ),
    PRODUCT_UPDATE("200", "product updated successfully", "")
    ;


    private final String code;
    private final String message;
    private final String param;
}
