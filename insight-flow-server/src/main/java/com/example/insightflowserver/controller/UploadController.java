package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.service.LabReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling lab report file uploads.
 *
 * This controller provides endpoints for uploading medical lab report files
 * (PDFs, images) and processing them using AI-powered OCR. The uploaded files
 * are analyzed, structured data is extracted, and the results are stored
 * in the database for later retrieval.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    /** Service for processing lab reports and managing database operations */
    @Autowired
    private LabReportService labReportService;

    /**
     * Handles file upload and processing of lab reports.
     *
     * This endpoint accepts multipart file uploads containing medical lab reports.
     * The file is processed using Google Gemini AI for OCR text extraction,
     * structured data is parsed, and the results are persisted to MongoDB.
     *
     * @param file the uploaded lab report file (PDF or image)
     * @param userId optional user identifier for associating the report with a user
     * @return ResponseEntity containing the processed LabReport object with extracted data
     * @throws IllegalArgumentException if the file is invalid or processing fails
     * @throws Exception if an unexpected error occurs during processing
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LabReport> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal(); // JWT subject = userId
            LabReport report = labReportService.extractAndPersist(file, userId);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
