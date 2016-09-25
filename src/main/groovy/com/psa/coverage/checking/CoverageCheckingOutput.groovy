package com.psa.coverage.checking

/**
 * This is the coverage checking output boundary
 */
interface CoverageCheckingOutput<VM> {
    /**
     * Generates a view model given the response.
     * @param response response received by the use case.
     */
    void processResponse(CoverageCheckingResponse response)
    /**
     * Gets the generated view model.
     * @return the view model got as a result of processing the response.
     */
    VM getViewModel()
}