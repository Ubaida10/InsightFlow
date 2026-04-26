package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabResult {
    private String test;
    private Double value;
    private String unit;
    private String referenceRange;
    private String status;
}
