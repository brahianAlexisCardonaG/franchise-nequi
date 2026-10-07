package com.franchise.project.api;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public class ValidatorBlockHoundIntegration implements BlockHoundIntegration {

    @Override
    public void applyTo(BlockHound.Builder builder) {
        builder.allowBlockingCallsInside(
                "org.hibernate.validator.resourceloading.PlatformResourceBundleLocator", "loadBundle");
    }
}
