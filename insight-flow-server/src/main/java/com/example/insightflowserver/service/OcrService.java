package com.example.insightflowserver.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class OcrService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private static final String MODEL = "gemini-2.5-flash";
    private static final String OCR_PROMPT = """
            Extract all text from this medical lab report exactly as it appears.
            Preserve all values, units, reference ranges, and section headers.
            Return only the extracted text — no commentary, no formatting.
            """;

    public String extractText(MultipartFile file) throws IOException {
        String mimeType = resolveMimeType(file);
        byte[] fileBytes = file.getBytes();

        Client client = Client.builder().apiKey(apiKey).httpOptions(HttpOptions.builder().apiVersion("v1").build()).build();
        
        Content content = Content.fromParts(
                Part.fromBytes(fileBytes, mimeType),
                Part.fromText(OCR_PROMPT)
        );

        GenerateContentResponse response = client.models.generateContent(MODEL, content, null);

        String extracted = response.text();
        log.info("Gemini extracted {} characters from {}", extracted.length(), file.getOriginalFilename());
        return extracted;
    }

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