package com.example.insightflowserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for InsightFlow Server.
 *
 * This is the entry point for the Spring Boot application that provides
 * lab report processing and storage functionality. The application integrates
 * with Google Gemini AI for OCR text extraction from medical lab reports
 * and stores the processed data in MongoDB Atlas.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@SpringBootApplication
public class InsightFlowServerApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * This method initializes the Spring application context, configures
     * all beans, and starts the embedded Tomcat server on the configured port.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(InsightFlowServerApplication.class, args);
    }

}
