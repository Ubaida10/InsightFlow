package com.example.insightflowserver.controller;

import com.example.insightflowserver.model.LabReport;
import com.example.insightflowserver.model.LabResult;
import com.example.insightflowserver.model.TrendPoint;
import com.example.insightflowserver.repositories.LabReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/trends")
@RequiredArgsConstructor
public class TrendController {
    private final LabReportRepository labReportRepository;

    @GetMapping("/{testName}")
    public ResponseEntity<List<TrendPoint>> getTrend(
            @PathVariable String testName,
            @RequestParam (defaultValue = "anonymous") String userId){
        List<LabReport> reports = labReportRepository.findByUserId(userId);
        List<TrendPoint> trendPoints = reports.stream()
                .flatMap(report -> report.getResults().stream()
                        .filter(result -> result.getTest()
                                .equalsIgnoreCase(testName))
                        .map(result -> new TrendPoint(
                                report.getUploadedAt().toString(),
                                result.getValue(),
                                result.getUnit(),
                                result.getStatus(),
                                result.getColorCode(),
                                report.getId()
                        ))
                )
                .collect(Collectors.toList());

        log.info("Found {} trend points for test '{}' user '{}'", trendPoints.size(), testName, userId);
        return ResponseEntity.ok(trendPoints);

    }

    public ResponseEntity<List<String>> getAvailableTests(
            @RequestParam (defaultValue = "anonymous") String userId) {
        List<LabReport> reports = labReportRepository.findByUserId(userId);
        List<String> tests = reports.stream()
                .flatMap(report -> report.getResults().stream())
                .map(LabResult::getTest)
                .distinct()
                .collect(Collectors.toList());

        log.info("Found {} unique tests for user '{}'", tests.size(), userId);
        return ResponseEntity.ok(tests);
    }
}
