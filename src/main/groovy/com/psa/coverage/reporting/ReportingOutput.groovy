package com.psa.coverage.reporting

/**
 * This is the output boundary for the reporting use case.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
interface ReportingOutput<VM> {
    /**
     * Generates a view model of the given type from the given response.
     * @param response response to process.
     */
    void processResponse(ReportingResponse response)
    /**
     * Returns the processed view model.
     * @return the processed view model.
     */
    VM getViewModel()

}