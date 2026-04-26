package com.example.insightflowserver.service;

import com.example.insightflowserver.constants.PromptConstants;
import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.repositories.LabReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Service class for processing and managing lab reports.
 *
 * This service handles the complete workflow of lab report processing:
 * extracting structured data from uploaded files using Google Gemini AI,
 * parsing the AI response, and persisting the results to MongoDB.
 * It integrates OCR capabilities with database operations.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LabReportService {

    /** Repository for lab report database operations */
    private final LabReportRepository labReportRepository;

    /** Google Gemini API key for AI-powered OCR */
    @Value("${gemini.api-key}")
    private String apiKey;

    /** Gemini AI model identifier for text extraction */
    private static final String MODEL = "gemini-2.5-flash";

    /**
     * Processes an uploaded file and persists the extracted lab report data.
     *
     * This method orchestrates the complete lab report processing workflow:
     * 1. Extracts structured data from the file using AI OCR
     * 2. Enriches the data with user and file metadata
     * 3. Persists the complete report to MongoDB
     *
     * @param file the uploaded lab report file (PDF or image)
     * @param userId identifier of the user uploading the report
     * @return the processed and persisted LabReport object
     * @throws IOException if file reading or processing fails
     */
    public LabReport extractAndPersist(MultipartFile file, String userId) throws IOException {
        // 1. Extract structured data
        LabReport report = extractStructureReport(file);

        // 2. Enrich with metadata
        report.setUserId(userId);
        report.setOriginalFileName(file.getOriginalFilename());
        report.setUploadedAt(LocalDateTime.now());

        // 3. Persist to MongoDB
        try {
            LabReport reportSaved = labReportRepository.save(report);
            log.info("Persisted lab report with id: {}", reportSaved.getId());
            return reportSaved;
        } catch(Exception e) {
            throw e;
        }

    }

    /**
     * Extracts structured lab report data from an uploaded file using AI.
     *
     * This method uses Google Gemini AI to perform OCR on the uploaded file
     * and extract structured medical data according to predefined prompts.
     * The AI response is parsed into a LabReport object.
     *
     * @param file the uploaded lab report file to process
     * @return LabReport object containing extracted structured data
     * @throws IOException if file processing or AI communication fails
     */
    public LabReport extractStructureReport(MultipartFile file) throws IOException {
        String mimeType = resolveMimeType(file);
        byte[] fileBytes = file.getBytes();

        Client client = Client.builder().apiKey(apiKey).httpOptions(HttpOptions.builder().apiVersion("v1").build()).build();

        Content content = Content.fromParts(
                Part.fromBytes(fileBytes, mimeType),
                Part.fromText(PromptConstants.LAB_REPORT_PROMPT)
        );

        GenerateContentResponse response = client.models.generateContent(MODEL, content, null);
        String rawJson = response.text();

        log.info("Raw Gemini response:\n{}", rawJson);

        return parseLabReport(rawJson);
    }

    /**
     * Parses raw JSON response from Gemini AI into a LabReport object.
     *
     * This method cleans the AI response (removing markdown formatting if present),
     * deserializes the JSON into a LabReport object, and logs the extracted results
     * for debugging purposes.
     *
     * @param rawJson the raw JSON string from Gemini AI response
     * @return parsed LabReport object with structured lab data
     * @throws RuntimeException if JSON parsing fails
     */
    private LabReport parseLabReport(String rawJson) {
        try {
            // Strip markdown fences if Gemini wraps in ```json ... ``` despite instructions
            String cleanedJson = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replace("```", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();
            LabReport labReport = mapper.readValue(cleanedJson, LabReport.class);

            labReport.getResults().forEach(r ->
                    log.info("  {} : {} {} [{}] → {}",
                            r.getTest(), r.getValue(), r.getUnit(),
                            r.getReferenceRange(), r.getStatus())
            );

            return labReport;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines the MIME type of an uploaded file.
     *
     * This method first checks the file's content type, and if it's generic
     * (application/octet-stream), it falls back to filename extension analysis
     * to determine the appropriate MIME type for supported file formats.
     *
     * @param file the uploaded file to analyze
     * @return the determined MIME type string
     * @throws IllegalArgumentException if the file type cannot be determined or is unsupported
     */
    private String resolveMimeType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/octet-stream")) {
            return contentType;
        }
        String name = file.getOriginalFilename();
        if (name == null) throw new IllegalArgumentException("Cannot determine file type");
        if (name.endsWith(".pdf"))                       return "application/pdf";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png"))                       return "image/png";
        throw new IllegalArgumentException("Unsupported file type: " + name);
    }
}