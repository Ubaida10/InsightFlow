package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
/**
 * MongoDB document representing a processed lab report.
 *
 * This entity stores the structured data extracted from uploaded medical
 * lab reports. It contains patient information, report metadata, and
 * a list of individual lab test results with their values and interpretations.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Document(collection = "lab_reports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabReport {

    /** Unique identifier for the lab report document */
    @Id
    private String id;

    /** Identifier of the user who uploaded the report */
    private String userId;

    /** Name of the patient whose lab results are contained in this report */
    private String patientName;

    /** Date when the lab tests were performed */
    private String reportDate;

    /** Original filename of the uploaded lab report file */
    private String originalFileName;

    /** Timestamp when the report was uploaded and processed */
    private LocalDateTime uploadedAt;

    /** List of individual lab test results extracted from the report */
    private List<LabResult> results;
}
