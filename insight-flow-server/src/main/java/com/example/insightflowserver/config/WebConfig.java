package com.example.insightflowserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for CORS (Cross-Origin Resource Sharing) settings.
 *
 * This configuration class implements WebMvcConfigurer to customize the
 * Spring MVC configuration, specifically handling CORS settings to allow
 * cross-origin requests from the Angular frontend application.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** Allowed origins for CORS requests, injected from application properties */
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Configures CORS mappings for API endpoints.
     *
     * This method sets up CORS configuration to allow the Angular frontend
     * to make requests to the Spring Boot backend. It allows all common HTTP
     * methods, headers, and credentials for API endpoints.
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
