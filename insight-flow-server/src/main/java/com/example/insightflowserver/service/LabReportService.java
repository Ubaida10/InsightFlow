package com.example.insightflowserver.service;

import com.example.insightflowserver.PromptConstants;
import com.example.insightflowserver.model.LabReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class LabReportService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private static final String MODEL = "gemini-2.5-flash";

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