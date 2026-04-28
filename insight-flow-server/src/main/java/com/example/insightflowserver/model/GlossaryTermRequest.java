package com.example.insightflowserver.model;

import lombok.Data;

/**
 * Request model for adding a new medical glossary term.
 *
 * This class represents the request payload for ingesting new medical terms
 * into the glossary database with their definitions, normal ranges, and categories.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Data
public class GlossaryTermRequest {
    /** The medical term to be added to the glossary */
    private String term;

    /** Detailed definition or explanation of the medical term */
    private String definition;

    /** Normal reference range for the term if applicable */
    private String normalRange;

    /** Category or classification of the medical term */
    private String category;
}
