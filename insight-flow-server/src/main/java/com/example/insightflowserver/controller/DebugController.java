package com.example.insightflowserver.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for debugging and diagnostic endpoints.
 *
 * This controller provides endpoints for troubleshooting and monitoring
 * the application's configuration and connectivity. It is primarily used
 * for development and debugging purposes to verify database connections
 * and configuration settings.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    /** MongoDB connection URI injected from application properties */
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    /**
     * Returns masked MongoDB configuration information for debugging.
     *
     * This endpoint provides visibility into the MongoDB connection configuration
     * without exposing sensitive credentials. The password is masked for security.
     * Used for troubleshooting database connectivity issues.
     *
     * @return a string containing the masked MongoDB URI for debugging purposes
     */
    @GetMapping("/mongo-config")
    public String getMongoConfig() {
        // Hide password for security
        String maskedUri = mongoUri.replaceAll(":(.*?)@", ":***@");
        return "MongoDB URI: " + maskedUri;
    }
}
