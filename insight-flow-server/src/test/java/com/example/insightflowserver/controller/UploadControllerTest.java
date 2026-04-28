package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.service.LabReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UploadController.
 *
 * Tests the file upload endpoints and error handling for lab report processing.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@WebMvcTest(UploadController.class)
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LabReportService labReportService;

    @Test
    void uploadFile_ShouldReturnLabReport_WhenProcessingSucceeds() throws Exception {
        // Given
        LabReport expectedReport = new LabReport();
        expectedReport.setPatientName("John Doe");

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "sample content".getBytes());

        when(labReportService.extractAndPersist(any(), any())).thenReturn(expectedReport);

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                .file(file)
                .param("userId", "user123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientName").value("John Doe"));
    }

    @Test
    void uploadFile_ShouldReturnBadRequest_WhenIllegalArgumentExceptionOccurs() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "sample content".getBytes());

        when(labReportService.extractAndPersist(any(), any()))
            .thenThrow(new IllegalArgumentException("Invalid file"));

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                .file(file)
                .param("userId", "user123"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void uploadFile_ShouldReturnInternalServerError_WhenUnexpectedExceptionOccurs() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "sample content".getBytes());

        when(labReportService.extractAndPersist(any(), any()))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                .file(file)
                .param("userId", "user123"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadFile_ShouldUseDefaultUserId_WhenUserIdNotProvided() throws Exception {
        // Given
        LabReport expectedReport = new LabReport();
        expectedReport.setPatientName("John Doe");

        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "sample content".getBytes());

        when(labReportService.extractAndPersist(any(), eq("anonymous"))).thenReturn(expectedReport);

        // When & Then
        mockMvc.perform(multipart("/api/upload")
                .file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientName").value("John Doe"));
    }
}