package com.example.insightflowserver.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for MongoDB Atlas connection and client settings.
 *
 * This class provides custom MongoDB configuration to ensure proper connection
 * to MongoDB Atlas cloud database. It configures the MongoDB client with
 * specific credentials, connection settings, and monitoring parameters to
 * avoid authorization issues with system databases.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Configuration
public class MongoDBConfig {

    /** MongoDB connection URI injected from application properties */
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    /**
     * Creates and configures a MongoDB client for Atlas connection.
     *
     * This method sets up the MongoDB client using the connection string
     * from application properties, which includes all necessary authentication
     * and connection details for MongoDB Atlas.
     *
     * @return configured MongoClient instance for MongoDB Atlas
     */
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToServerSettings(builder ->
                builder.heartbeatFrequency(60, TimeUnit.SECONDS)
                    .minHeartbeatFrequency(30, TimeUnit.SECONDS))
            .build();
        return MongoClients.create(settings);
    }

    /**
     * Creates a MongoTemplate for database operations.
     *
     * This template provides high-level abstraction for MongoDB operations
     * and is configured to work with the InsightFlow database.
     *
     * @param mongoClient the configured MongoDB client
     * @return MongoTemplate instance for database operations
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "InsightFlow");
    }
}
