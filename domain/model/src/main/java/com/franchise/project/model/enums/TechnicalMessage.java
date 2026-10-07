package com.franchise.project.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {
    INTERNAL_ERROR("500", "Something went wrong, please try again later"),
    INVALID_REQUEST("400", "Malformed request, please verify the data"),
    INVALID_PARAMETERS("400", "Invalid or missing parameters, please verify the data"),
    RESOURCE_ALREADY_EXISTS("409", "The resource already exists"),
    SERVICE_UNAVAILABLE("503", "Service temporarily unavailable, please try again later"),

    FRANCHISE_CREATED("201", "Franchise created successfully"),
    FRANCHISE_NOT_EXISTS("404", "The franchise does not exist"),
    FRANCHISE_ALREADY_EXISTS("409", "A franchise with this name already exists"),
    FRANCHISE_BRANCH_PRODUCT_FOUND("200", "Largest stock product per branch retrieved successfully"),
    FRANCHISE_UPDATE("200", "Franchise updated successfully"),

    BRANCH_CREATED("201", "Branch created successfully"),
    BRANCH_NOT_EXISTS("404", "The branch does not exist"),
    BRANCH_ALREADY_EXISTS("409", "A branch with this name already exists in the franchise"),
    BRANCH_UPDATE("200", "Branch updated successfully"),

    PRODUCT_CREATED("201", "Product created successfully"),
    PRODUCT_NOT_EXISTS("404", "The product does not exist"),
    PRODUCT_ALREADY_EXISTS("409", "A product with this name already exists in the branch"),
    PRODUCT_STOCK_INVALID("400", "The product stock must be greater than or equal to zero"),
    PRODUCT_DELETED("200", "Product deleted successfully"),
    PRODUCT_UPDATE("200", "Product updated successfully");

    private final String code;
    private final String message;
}
