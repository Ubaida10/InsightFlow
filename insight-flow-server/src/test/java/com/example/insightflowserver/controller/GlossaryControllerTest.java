package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.GlossaryTermRequest;
import com.example.insightflowserver.service.GlossaryIngestionService;
import com.example.insightflowserver.service.RagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlossaryController.
 *
 * Tests the medical glossary management endpoints including term explanations
 * and administrative term additions.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@ExtendWith(MockitoExtension.class)
class GlossaryControllerTest {

    @Mock
    private RagService ragService;

    @Mock
    private GlossaryIngestionService glossaryIngestionService;

    @InjectMocks
    private GlossaryController glossaryController;

    @Test
    void explainTerm_ShouldReturnExplanation_WhenTermIsProvided() {
        // Given
        String term = "Hemoglobin";
        String expectedExplanation = "Hemoglobin is a protein in red blood cells that carries oxygen.";
        when(ragService.explainTerm(term)).thenReturn(expectedExplanation);

        // When
        ResponseEntity<String> response = glossaryController.explainTerm(term);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedExplanation, response.getBody());
        verify(ragService).explainTerm(term);
    }

    @Test
    void explainTerm_ShouldHandleEmptyTerm() {
        // Given
        String term = "";
        String expectedExplanation = "Please provide a valid medical term.";
        when(ragService.explainTerm(term)).thenReturn(expectedExplanation);

        // When
        ResponseEntity<String> response = glossaryController.explainTerm(term);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedExplanation, response.getBody());
    }

    @Test
    void explainTerm_ShouldHandleSpecialCharacters() {
        // Given
        String term = "C-reactive protein (CRP)";
        String expectedExplanation = "CRP is a marker of inflammation.";
        when(ragService.explainTerm(term)).thenReturn(expectedExplanation);

        // When
        ResponseEntity<String> response = glossaryController.explainTerm(term);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedExplanation, response.getBody());
    }

    @Test
    void addTerm_ShouldReturnSuccessMessage_WhenTermIsAdded() {
        // Given
        GlossaryTermRequest request = new GlossaryTermRequest();
        request.setTerm("NewTest");
        request.setDefinition("A new medical test");
        request.setNormalRange("0-10");
        request.setCategory("Cardiology");

        // When
        ResponseEntity<String> response = glossaryController.addTerm(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Term added: NewTest"));
        verify(glossaryIngestionService).ingestSingleTerm(request);
    }

    @Test
    void addTerm_ShouldHandleNullRequest() {
        // Given - null request

        // When & Then - should handle gracefully or throw appropriate exception
        assertThrows(NullPointerException.class, () -> {
            glossaryController.addTerm(null);
        });
    }

    @Test
    void addTerm_ShouldHandleEmptyTerm() {
        // Given
        GlossaryTermRequest request = new GlossaryTermRequest();
        request.setTerm("");
        request.setDefinition("Empty term definition");

        // When
        ResponseEntity<String> response = glossaryController.addTerm(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Term added: "));
        verify(glossaryIngestionService).ingestSingleTerm(request);
    }
}
