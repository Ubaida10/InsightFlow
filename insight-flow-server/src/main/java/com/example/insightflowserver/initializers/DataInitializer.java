package com.example.insightflowserver.initializers;

import com.example.insightflowserver.service.GlossaryIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Application startup initializer for medical glossary data ingestion.
 *
 * This component implements ApplicationRunner to automatically ingest
 * the medical glossary into MongoDB Atlas Vector Search during application
 * startup if the feature is enabled via configuration properties.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    /** Service for ingesting glossary terms into the vector store */
    private final GlossaryIngestionService glossaryIngestionService;

    /** Flag to enable/disable glossary ingestion on application startup */
    @Value("${app.glossary.ingest-on-startup:false}")
    private boolean ingestOnStartUp;

    /**
     * Executes on application startup to initialize glossary data.
     *
     * If enabled via configuration, this method triggers glossary ingestion
     * to populate the MongoDB Atlas Vector Search with medical terms and embeddings.
     *
     * @param args application command line arguments
     * @throws Exception if glossary ingestion fails
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (ingestOnStartUp) {
            log.info("Ingesting glossary on startup...");
            glossaryIngestionService.ingestGlossary();
        } else {
            log.info("Skipping glossary ingestion on startup. Set 'app.glossary.ingest-on-startup=true' to enable.");
        }
    }
}
