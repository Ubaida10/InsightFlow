package com.example.insightflowserver.controller;

import com.example.insightflowserver.service.OcrService;
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
    private OcrService ocrService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received file: {}", file.getOriginalFilename());
            String extractedText = ocrService.extractText(file);
            log.info("Extracted text: {}", extractedText.substring(0, Math.min(100, extractedText.length())));
            return ResponseEntity.ok(extractedText);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.internalServerError().body("OCR processing failed: " + e.getMessage());
        }
    }
}
