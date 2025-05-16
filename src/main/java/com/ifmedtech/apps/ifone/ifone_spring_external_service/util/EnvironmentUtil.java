package com.ifmedtech.apps.ifone.ifone_spring_external_service.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtil {
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(activeProfile);
    }

    public boolean isLocal() {
        return "local".equalsIgnoreCase(activeProfile);
    }
}
