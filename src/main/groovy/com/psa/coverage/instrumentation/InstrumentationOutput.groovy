package com.psa.coverage.instrumentation

/**
 * This interface defines the output boundary for the instrumentation.
 */
interface InstrumentationOutput<VM> {
    /**
     * Processes a response to generate a view model.
     * @param response is the response to process.
     */
    void processResponse(InstrumentationResponse response)
    /**
     * Returns the resulting view model.
     * @return the resulting view model.
     */
    VM getViewModel()
}