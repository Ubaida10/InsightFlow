package com.example.insightflowserver.service;

import com.example.insightflowserver.constants.MedicalDisclaimer;
import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.model.LabResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SafetyShieldService {
    /**
     * Applies safety analysis to a lab report.
     * Flags results outside reference ranges and attaches a disclaimer.
     */
    public LabReport applyShield(LabReport labReport) {
        if (labReport.getResults() != null){
            labReport.getResults().forEach(this::analyzeResult);
        }
        labReport.setDisclaimer(MedicalDisclaimer.MEDICAL_DISCLAIMER);
        labReport.setAnalyzedAt(LocalDateTime.now());

        long flaggedCount = labReport.getResults().stream()
                .filter(LabResult::isFlagged)
                .count();

        log.info("Safety Shield applied to report {}: {} results flagged", labReport.getId(), flaggedCount);
        return labReport;
    }

    private void analyzeResult(LabResult labResult) {
        if (labResult.getReferenceRange() == null || labResult.getValue() == null) {
            labResult.setColorCode("YELLOW");
            labResult.setUrgencyLevel("ROUTINE");
            labResult.setFlagged(false);
            return;
        }

        RangeResult range = parseRange(labResult.getReferenceRange());

        if (range == null) {
            // Can't parse range — mark as yellow but don't flag
            labResult.setColorCode("YELLOW");
            labResult.setUrgencyLevel("ROUTINE");
            labResult.setFlagged(false);
            return;
        }

        double value = labResult.getValue();
        double deviationPercent = calculateDeviation(value, range);

        if (range.min != null && value < range.min) {
            applyFlag(labResult, "low", value, range, deviationPercent);
        } else if (range.max != null && value > range.max) {
            applyFlag(labResult, "high", value, range, deviationPercent);
        } else {
            // Within range
            labResult.setStatus("normal");
            labResult.setColorCode("GREEN");
            labResult.setUrgencyLevel("ROUTINE");
            labResult.setFlagged(false);
            labResult.setFlagReason(null);
        }
    }

    private void applyFlag(LabResult result, String direction,
                           double value, RangeResult range, double deviation) {
        result.setStatus(direction);
        result.setFlagged(true);

        String boundary = direction.equals("high")
                ? String.format("exceeds max %.1f", range.max)
                : String.format("below min %.1f", range.min);

        result.setFlagReason(String.format("Value %.1f %s %s (%.1f%% outside range)",
                value, result.getUnit(), boundary, deviation));

        // Assign color and urgency based on deviation
        if (deviation > 50.0) {
            result.setColorCode("RED");
            result.setUrgencyLevel("URGENT");
        } else if (deviation > 20.0) {
            result.setColorCode("RED");
            result.setUrgencyLevel("ELEVATED");
        } else {
            result.setColorCode("YELLOW");
            result.setUrgencyLevel("ELEVATED");
        }
    }

    /**
     * Parses reference ranges like:
     * "70-100", "< 5.7", "> 40", "3.5 - 5.0", "< 200 mg/dL"
     */
    private RangeResult parseRange(String range) {
        if (range == null || range.isBlank()) return null;

        String cleaned = range.trim()
                .replaceAll("[a-zA-Z/%]", "")  // remove units like mg/dL, %
                .trim();

        try {
            // Pattern: "70-100" or "3.5 - 5.0"
            if (cleaned.matches(".*\\d+\\.?\\d*\\s*-\\s*\\d+\\.?\\d*.*")) {
                String[] parts = cleaned.split("-");
                return new RangeResult(
                        Double.parseDouble(parts[0].trim()),
                        Double.parseDouble(parts[1].trim())
                );
            }

            // Pattern: "< 5.7" or "<5.7"
            if (cleaned.matches("\\s*<\\s*\\d+\\.?\\d*\\s*")) {
                double max = Double.parseDouble(cleaned.replaceAll("[^\\d.]", "").trim());
                return new RangeResult(null, max);
            }

            // Pattern: "> 40" or ">40"
            if (cleaned.matches("\\s*>\\s*\\d+\\.?\\d*\\s*")) {
                double min = Double.parseDouble(cleaned.replaceAll("[^\\d.]", "").trim());
                return new RangeResult(min, null);
            }

        } catch (NumberFormatException e) {
            log.warn("Could not parse reference range: '{}'", range);
        }

        return null;
    }

    private double calculateDeviation(double value, RangeResult range) {
        if (range.max != null && value > range.max) {
            return ((value - range.max) / range.max) * 100;
        }
        if (range.min != null && value < range.min) {
            return ((range.min - value) / range.min) * 100;
        }
        return 0;
    }

    // Simple inner class to hold parsed range
    private record RangeResult(Double min, Double max) {}

}
