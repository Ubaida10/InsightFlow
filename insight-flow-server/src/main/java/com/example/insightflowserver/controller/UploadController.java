package com.example.insightflowserver.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        System.out.println("Received file: " + file.getOriginalFilename());
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

}
