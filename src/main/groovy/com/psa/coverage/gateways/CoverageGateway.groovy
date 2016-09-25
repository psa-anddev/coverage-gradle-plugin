package com.psa.coverage.gateways

import com.psa.coverage.configuration.CoverageChecking
import com.psa.coverage.configuration.Instrumentation
import com.psa.coverage.configuration.Reporting

/**
 * This interface defines the methods that can be used to implement different coverage tools.
 */
interface CoverageGateway {
    /**
     * Instrument the classes.
     * @param instrumentation Instrumentation configuration.
     */
    void instrument(Instrumentation instrumentation)
    /**
     * Generates the coverage report.
     * @param reporting is the reporting configuration
     */
    void generateReport(Reporting reporting)

    /**
     * Checks the coverage results.
     *
     * @param checking is the coverage checking configuration.
     */
    void check(CoverageChecking checking)
    /**
     * Merges the reports into the data file.
     * @param dataFile destination file for the reports, when empty or null the coverage tool default is used.
     * @param reports list of reports to merge.
     */
    void merge(String dataFile, List<String> reports)
}