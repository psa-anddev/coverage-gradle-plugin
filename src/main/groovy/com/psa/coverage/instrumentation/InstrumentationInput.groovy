package com.psa.coverage.instrumentation

/**
 * This is the instrumentation input interface.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
interface InstrumentationInput {
    /**
     * Instrument the classes according to the data in the request.
     * @param request is the instrumentation request object.
     * @param output is the presenter that will display the results.
     */
    void instrument(InstrumentationRequest request, InstrumentationOutput output)
}