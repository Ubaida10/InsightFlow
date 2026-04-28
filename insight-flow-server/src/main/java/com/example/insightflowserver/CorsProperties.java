package com.example.insightflowserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties class for CORS (Cross-Origin Resource Sharing) settings.
 *
 * This class binds configuration properties with the "cors" prefix from
 * application.properties files, allowing externalized configuration of
 * allowed origins for cross-origin requests.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * The allowed origins for CORS requests.
     * This can be a comma-separated list of URLs or "*" for all origins.
     */
    private String allowedOrigins;

    /**
     * Gets the allowed origins for CORS requests.
     *
     * @return the comma-separated string of allowed origins
     */
    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Sets the allowed origins for CORS requests.
     *
     * @param allowedOrigins the comma-separated string of allowed origins to set
     */
    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
