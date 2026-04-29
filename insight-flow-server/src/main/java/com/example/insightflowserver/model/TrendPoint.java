package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendPoint {
    private String date;
    private Double value;
    private String unit;
    private String status;
    private String colorCode;
    private String reportId;
}
