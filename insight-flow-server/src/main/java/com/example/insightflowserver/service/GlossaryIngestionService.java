package com.example.insightflowserver.service;

import com.example.insightflowserver.model.GlossaryTermRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Service for ingesting medical glossary terms into the vector store.
 *
 * This service manages the loading and processing of medical terms from JSON files
 * and individual term additions into MongoDB Atlas Vector Search for semantic search
 * capabilities. It converts terms into embeddings for RAG (Retrieval-Augmented Generation).
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlossaryIngestionService {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper =  new ObjectMapper();

    /**
     * Ingests the entire medical glossary from a JSON resource file.
     *
     * Loads all medical terms from medical-glossary.json, converts them to embedded
     * documents, and stores them in MongoDB Atlas Vector Search for semantic search.
     *
     * @throws IOException if the glossary file cannot be read
     */
    public void ingestGlossary() throws IOException {
        log.info("Starting glossary ingestion...");

        // Load JSON from resources
        ClassPathResource resource = new ClassPathResource("medical-glossary.json");
        List<Map<String, String>> terms = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}
        );

        // Convert each term to a Spring AI Document
        List<Document> documents = terms.stream().map(term -> {
            // The text that gets embedded — include term + definition for richer search
            String content = String.format("Term: %s. Definition: %s. Normal range: %s",
                    term.get("term"),
                    term.get("definition"),
                    term.get("normalRange")
            );

            // Metadata stored alongside the vector — retrieved with results
            Map<String, Object> metadata = Map.of(
                    "term", term.get("term"),
                    "category", term.get("category"),
                    "normalRange", term.get("normalRange")
            );

            return new Document(content, metadata);
        }).toList();

        vectorStore.add(documents);
        log.info("Ingested {} glossary terms into Atlas Vector Search", documents.size());
    }

    /**
     * Adds a single medical term to the glossary vector store.
     *
     * Ingests a new term with its definition, normal range, and category into the
     * vector store, making it immediately available for RAG-based explanations.
     *
     * @param request the GlossaryTermRequest containing term details to ingest
     */
    public void ingestSingleTerm(GlossaryTermRequest request) {
        String content = String.format("Term: %s. Definition: %s. Normal range: %s",
                request.getTerm(),
                request.getDefinition(),
                request.getNormalRange()
        );

        Document document = Document.builder()
                .text(content)
                .metadata("term", request.getTerm())
                .metadata("category", request.getCategory())
                .metadata("normalRange", request.getNormalRange())
                .build();

        vectorStore.add(List.of(document));
        log.info("Added term to vector store: {}", request.getTerm());
    }
}