package com.example.insightflowserver.config;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.google.genai.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for AI services and embeddings.
 *
 * This configuration establishes beans for Google Gemini AI embeddings
 * and chat client functionality. It configures the embedding model
 * with the necessary API credentials and options.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.google.genai.embedding.api-key}")
    private String embeddingApiKey;

    /**
     * Creates and configures the embedding model bean.
     *
     * Initializes a GoogleGenAiTextEmbeddingModel configured with API credentials
     * and model-specific options for text embedding generation.
     *
     * @return an EmbeddingModel instance configured with Gemini embeddings
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        Client client = Client.builder()
                .apiKey(embeddingApiKey)
                .httpOptions(HttpOptions.builder().apiVersion("v1").build())
                .build();

        GoogleGenAiEmbeddingConnectionDetails connectionDetails =
                GoogleGenAiEmbeddingConnectionDetails.builder()
                        .apiKey(embeddingApiKey)
                        .build();

        GoogleGenAiTextEmbeddingOptions options =
                GoogleGenAiTextEmbeddingOptions.builder()
                        .model("gemini-embedding-001")
                        .build();

        return new GoogleGenAiTextEmbeddingModel(connectionDetails, options);
    }

    /**
     * Creates and configures the chat client bean.
     *
     * Builds a ChatClient instance from the provided builder for handling
     * conversational AI interactions with the Gemini model.
     *
     * @param builder the ChatClient.Builder to configure
     * @return a configured ChatClient instance
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}