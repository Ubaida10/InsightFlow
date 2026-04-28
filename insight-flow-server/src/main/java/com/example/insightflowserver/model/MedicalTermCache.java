package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "glossary_cache")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalTermCache {
    @Id
    private String term;

    private String content;
    private String category;
    private String normalRange;
}
