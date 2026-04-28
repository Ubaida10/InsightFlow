package com.example.insightflowserver.constants;

/**
 * Constants class containing AI prompts and templates.
 *
 * This class holds predefined prompt templates used for interacting
 * with AI services, specifically Google Gemini for medical document
 * processing and data extraction.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
public class PromptConstants {

    /**
     * AI prompt template for extracting structured data from lab reports.
     *
     * This prompt instructs Google Gemini AI to analyze medical lab reports
     * (PDFs or images) and extract structured data including patient information
     * and test results. The prompt specifies exact JSON output format and
     * validation rules to ensure consistent, parseable responses.
     *
     * Key extraction requirements:
     * - Patient name and report date
     * - All laboratory test results with values, units, reference ranges
     * - Status interpretation (normal/high/low) based on reference ranges
     * - Strict JSON format without markdown formatting
     */
    public static final String LAB_REPORT_PROMPT = """
            You are a medical data extraction AI.
            Analyze this lab report image or PDF and extract all test results.

            Return ONLY a valid JSON object — no markdown, no backticks, no explanation.

            Use exactly this structure:
            {
               "patientName": "string or null",
               "reportDate": "string or null",
               "results": [
               {
                 "test": "test name",
                 "value": numeric value as number,
                 "unit": "unit string",
                 "referenceRange": "e.g. 70-100 or null",
                 "status": "normal | high | low | null"
               }
               ]
            }

            Rules:
               - value must always be a number, never a string
               - If a field is missing from the report, use null
               - status: compare value against referenceRange if available
               - Extract every test you can find, do not skip any
            """;

    public static final String EXPLAIN_FROM_CONTEXT_PROMPT = """
                You are a friendly medical assistant explaining lab results to patients.
            
                Use the context below to explain "%s" if it is relevant.
                If the context is NOT about "%s", ignore it and use your own medical knowledge instead.
            
                Always:
                    - Use simple everyday language a patient can understand
                    - Mention what the test measures
                    - Mention the typical normal range
                    - Explain what high or low levels might mean
                    - Keep it brief and jargon-free
            
                Context:
                %s
            """;

    public static final String EXPLAIN_FROM_GEMINI_KNOWLEDGE = """
                You are a friendly medical assistant explaining lab results to patients.
                Explain the medical lab test "%s" in simple terms a patient can understand.
                Include:
                    - What it measures
                    - Typical normal range
                    - What high or low levels might mean
                Keep it brief, friendly, and jargon-free.
            """;
}
