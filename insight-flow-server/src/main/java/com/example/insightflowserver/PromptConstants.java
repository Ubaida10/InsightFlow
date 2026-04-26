package com.example.insightflowserver;

public class PromptConstants {
    public static final String LAB_REPORT_PROMPT = """
            You are a medical data extraction AI.
            Analyze this lab report image or PDF and extract all test results.
            
            Return ONLY a valid JSON object — no markdown, no backticks, no explanation.
            
            Use exactly this structure:
            {
               "patientName": "string or null",
               "reportDate": "string or null",
               "results": [
               {
                 "test": "test name",
                 "value": numeric value as number,
                 "unit": "unit string",
                 "referenceRange": "e.g. 70-100 or null",
                 "status": "normal | high | low | null"
               }
               ]
            }
            
            Rules:
               - value must always be a number, never a string
               - If a field is missing from the report, use null
               - status: compare value against referenceRange if available
               - Extract every test you can find, do not skip any
            """;
}
