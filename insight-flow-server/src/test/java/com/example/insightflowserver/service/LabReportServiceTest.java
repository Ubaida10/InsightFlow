package com.example.insightflowserver.service;

import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.repositories.LabReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LabReportService.
 *
 * Tests the lab report processing functionality including OCR extraction,
 * data parsing, and database persistence operations.
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@ExtendWith(MockitoExtension.class)
class LabReportServiceTest {

    @Mock
    private LabReportRepository labReportRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private LabReportService labReportService;

    private LabReport sampleLabReport;

    @BeforeEach
    void setUp() {
        // Set the apiKey field that would normally be injected by @Value
        ReflectionTestUtils.setField(labReportService, "apiKey", "test-api-key");

        sampleLabReport = new LabReport();
        sampleLabReport.setPatientName("John Doe");
        sampleLabReport.setReportDate("2024-01-15");
        sampleLabReport.setResults(List.of());
    }

    @Test
    void extractAndPersist_ShouldHandleIOException() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenThrow(new IOException("File read error"));

        // When & Then
        assertThrows(IOException.class, () -> {
            labReportService.extractAndPersist(multipartFile, "user123");
        });

        // Verify that repository save was never called due to the exception
        verify(labReportRepository, never()).save(any(LabReport.class));
    }

    @Test
    void extractAndPersist_ShouldUseDefaultUserId_WhenUserIdNotProvided() throws IOException {
        // Given - simulate the service working by mocking repository save
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenReturn("sample content".getBytes());
        when(labReportRepository.save(any(LabReport.class))).thenReturn(sampleLabReport);

        // When
        LabReport result = labReportService.extractAndPersist(multipartFile, null);

        // Then
        assertNotNull(result);
        assertEquals("anonymous", result.getUserId());
        verify(labReportRepository).save(any(LabReport.class));
    }

    @Test
    void extractAndPersist_ShouldUseProvidedUserId() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenReturn("sample content".getBytes());
        when(labReportRepository.save(any(LabReport.class))).thenReturn(sampleLabReport);

        // When
        LabReport result = labReportService.extractAndPersist(multipartFile, "customUser");

        // Then
        assertNotNull(result);
        assertEquals("customUser", result.getUserId());
        verify(labReportRepository).save(any(LabReport.class));
    }

    @Test
    void extractAndPersist_ShouldHandleRepositoryException() throws IOException {
        // Given
        lenient().when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        lenient().when(multipartFile.getContentType()).thenReturn("application/pdf");
        lenient().when(multipartFile.getBytes()).thenReturn("sample content".getBytes());
        when(labReportRepository.save(any(LabReport.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            labReportService.extractAndPersist(multipartFile, "user123");
        });
    }
}
