package com.example.insightflowserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing an individual lab test result.
 *
 * This class encapsulates the data for a single laboratory test result,
 * including the test name, measured value, unit of measurement, reference
 * range, and status interpretation (normal/high/low).
 *
 * @author Muhammad Abu Ubaida Aljerah
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabResult {

    /** Name of the laboratory test performed */
    private String test;

    /** Numerical value of the test result */
    private Double value;

    /** Unit of measurement for the test result */
    private String unit;

    /** Normal reference range for this test */
    private String referenceRange;

    /** Status interpretation of the result */
    private String status;
}
