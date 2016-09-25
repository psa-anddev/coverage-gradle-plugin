package com.psa.coverage.reporting

/**
 * This is the input boundary for the reporting use case.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
interface ReportingInput {
    void generateReports(ReportingRequest request, ReportingOutput output)
}
