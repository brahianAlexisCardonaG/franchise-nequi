package com.franchise.project.api.util.openapi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenApiExamples {

    public static final String INVALID_PARAMETERS = """
            {"code":"400","message":"Invalid or missing parameters, please verify the data","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"400","message":"Invalid or missing parameters, please verify the data"}]}""";

    public static final String MALFORMED_REQUEST = """
            {"code":"400","message":"Malformed request, please verify the data","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"400","message":"Malformed request, please verify the data"}]}""";

    public static final String PRODUCT_STOCK_INVALID = """
            {"code":"400","message":"The product stock must be greater than or equal to zero","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"400","message":"The product stock must be greater than or equal to zero"}]}""";

    public static final String FRANCHISE_NOT_FOUND = """
            {"code":"404","message":"The franchise does not exist","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"404","message":"The franchise does not exist"}]}""";

    public static final String BRANCH_NOT_FOUND = """
            {"code":"404","message":"The branch does not exist","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"404","message":"The branch does not exist"}]}""";

    public static final String PRODUCT_NOT_FOUND = """
            {"code":"404","message":"The product does not exist","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"404","message":"The product does not exist"}]}""";

    public static final String FRANCHISE_ALREADY_EXISTS = """
            {"code":"409","message":"A franchise with this name already exists","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"409","message":"A franchise with this name already exists"}]}""";

    public static final String BRANCH_ALREADY_EXISTS = """
            {"code":"409","message":"A branch with this name already exists in the franchise","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"409","message":"A branch with this name already exists in the franchise"}]}""";

    public static final String PRODUCT_ALREADY_EXISTS = """
            {"code":"409","message":"A product with this name already exists in the branch","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"409","message":"A product with this name already exists in the branch"}]}""";

    public static final String INTERNAL_ERROR = """
            {"code":"500","message":"Something went wrong, please try again later","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"500","message":"Something went wrong, please try again later"}]}""";

    public static final String SERVICE_UNAVAILABLE = """
            {"code":"503","message":"Service temporarily unavailable, please try again later","date":"2026-01-01T12:00:00Z",\
            "errors":[{"code":"503","message":"Service temporarily unavailable, please try again later"}]}""";
}
