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

    //franchise
    FRANCHISE_CREATED("201", "Franchise created successfully", ""),
    FRANCHISE_NOT_EXISTS("400"," The Franchise are not registered." ,"" ),
    FRANCHISE_ALREADY_EXISTS("400"," The Franchise already found registered." ,"" ),
    FRANCHISE_ID_REQUIRED("400"," The Franchise is required." ,"" ),
    FRANCHISE_BRANCH_PRODUCT_FOUND("200", "Products associates a branches by franchiseId", ""),

    //branch
    BRANCH_CREATED("201", "Branch created successfully", ""),
    BRANCH_NOT_EXISTS("400"," The Branch are not registered." ,"" ),
    BRANCH_ALREADY_EXISTS("400"," The Branch already found registered." ,"" ),

    //product
    PRODUCT_CREATED("201", "Product created successfully", ""),
    PRODUCT_NOT_EXISTS("400"," The Product are not registered." ,"" ),
    PRODUCT_ID_REQUIRED("400"," The Product is required." ,"" ),
    PRODUCT_ALREADY_EXISTS("400"," The Product already found registered." ,"" ),
    PRODUCT_BRANCH_DELETE("200"," The Product deleted of branch successfully." ,"" ),
    PRODUCT_UPDATE("200", "product updated successfully", "")
    ;


    private final String code;
    private final String message;
    private final String param;
}
