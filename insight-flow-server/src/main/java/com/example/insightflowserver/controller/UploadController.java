package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.service.LabReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private LabReportService labReportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LabReport> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received file: {}", file.getOriginalFilename());
            LabReport report = labReportService.extractStructureReport(file);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
