package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.GlossaryTermRequest;
import com.example.insightflowserver.service.GlossaryIngestionService;
import com.example.insightflowserver.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for medical glossary management and term explanations.
 *
 * This controller provides endpoints for retrieving medical term explanations
 * using RAG (Retrieval-Augmented Generation) and for managing glossary entries.
 * It integrates with the RagService for semantic search and explanations,
 * and the GlossaryIngestionService for administrative term additions.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GlossaryController {

    private final RagService ragService;
    private final GlossaryIngestionService glossaryIngestionService;

    /**
     * Retrieves an explanation for a medical term using RAG.
     *
     * This endpoint searches the medical glossary vector store and uses
     * Gemini AI to generate a contextual explanation of the requested term.
     *
     * @param term the medical term to explain
     * @return ResponseEntity containing the explanation text
     */
    @GetMapping("/explain/{term}")
    public ResponseEntity<String> explainTerm(@PathVariable String term) {
        String explanation = ragService.explainTerm(term);
        return ResponseEntity.ok(explanation);
    }

    /**
     * Adds a new term to the medical glossary.
     *
     * This is an administrative endpoint for ingesting new medical terms
     * and their definitions into the glossary database with embeddings.
     *
     * @param request the GlossaryTermRequest containing term and definition
     * @return ResponseEntity with confirmation message
     */
    @PostMapping("/admin/glossary")
    public ResponseEntity<String> addTerm(@RequestBody GlossaryTermRequest request) {
        glossaryIngestionService.ingestSingleTerm(request);
        return ResponseEntity.ok("Term added: " + request.getTerm());
    }
}