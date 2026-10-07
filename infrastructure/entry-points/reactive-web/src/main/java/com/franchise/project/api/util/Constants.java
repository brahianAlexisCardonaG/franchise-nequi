package com.franchise.project.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String PATH_FRANCHISE = "/api/v1/franchise";
    public static final String PATH_FRANCHISE_UPDATE_NAME = "/api/v1/franchise/name";
    public static final String FRANCHISE_ID_PATH_VARIABLE = "franchiseId";
    public static final String PATH_FRANCHISE_TOP_STOCK_PRODUCTS =
            "/api/v1/franchise/{" + FRANCHISE_ID_PATH_VARIABLE + "}/top-stock-products";

    public static final String PATH_POST_BRANCH = "/api/v1/branch";
    public static final String PATH_BRANCH_UPDATE_NAME = "/api/v1/branch/name";

    public static final String PATH_PRODUCT = "/api/v1/product";
    public static final String PATH_PRODUCT_UPDATE_STOCK = "/api/v1/product/stock";
    public static final String PATH_PRODUCT_UPDATE_NAME = "/api/v1/product/name";
    public static final String PRODUCT_ID_PATH_VARIABLE = "productId";
    public static final String PATH_PRODUCT_BY_ID = "/api/v1/product/{" + PRODUCT_ID_PATH_VARIABLE + "}";
}
