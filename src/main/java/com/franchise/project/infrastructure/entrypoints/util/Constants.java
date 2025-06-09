package com.franchise.project.infrastructure.entrypoints.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String X_MESSAGE_ID = "x-message-id";
    public static final String FRANCHISE_ERROR = "Error on Franchise - [ERROR]";

    //franchise
    public static final String PATH_FRANCHISE
            = "/api/v1/franchise";
    public static final String PATH_FRANCHISE_UPDATE_NAME
            = "/api/v1/franchise/name";

    //branch
    public static final String PATH_POST_BRANCH
            = "/api/v1/branch";
    public static final String PATH_BRANCH_UPDATE_NAME
            = "/api/v1/branch/name";

    //product
    public static final String PATH_PRODUCT
            = "/api/v1/product";
    public static final String PATH_PRODUCT_UPDATE_STOCK
            = "/api/v1/product/stock";
    public static final String PATH_PRODUCT_UPDATE_NAME
            = "/api/v1/product/name";

}
