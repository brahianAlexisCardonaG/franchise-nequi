package com.franchise.project.infrastructure.entrypoints.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String X_MESSAGE_ID = "x-message-id";
    public static final String FRANCHISE_ERROR = "Error on Franchise - [ERROR]";

    //franchise
    public static final String PATH_POST_FRANCHISE
            = "/api/v1/franchise";

    //branch
    public static final String PATH_POST_BRANCH
            = "/api/v1/branch";
}
