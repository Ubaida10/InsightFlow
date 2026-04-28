package com.example.insightflowserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for basic application health and greeting endpoints.
 *
 * This controller provides simple endpoints for testing the application's
 * availability and basic functionality. It serves as a health check endpoint
 * and demonstrates that the server is running correctly.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@RestController
public class HelloController {

    /**
     * Returns a greeting message from the InsightFlow Server.
     *
     * This endpoint serves as a basic health check to verify that the
     * application is running and responding to HTTP requests.
     *
     * @return a greeting string indicating the server is operational
     */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from InsightFlow Server!";
    }
}
