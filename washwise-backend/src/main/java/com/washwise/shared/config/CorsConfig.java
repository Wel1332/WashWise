package com.washwise.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Static resource handler for uploaded files. CORS itself is configured in
 * {@link SecurityConfig} so there is a single source of truth for allowed
 * origins.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads/profile-images}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
