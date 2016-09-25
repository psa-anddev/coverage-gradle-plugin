package com.psa.coverage.instrumentation

import com.psa.coverage.configuration.Instrumentation
import com.psa.coverage.helpers.CoverageContext

/**
 * This is the instrumentation interactor.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class InstrumentationInteractor implements InstrumentationInput {
    void instrument(InstrumentationRequest request, InstrumentationOutput output) {
        CoverageContext.coverageGateway.instrument(generateInstrumentationConfiguration(request))
        output.processResponse(new InstrumentationResponse(instrumentationFilePath: getInstrumentationFilePath(request)))
    }

    /**
     * Generates the instrumentation configuration object given the instrumentation request.
     * @param request is the request object.
     * @return the instrumentation configuration object
     */
    private static Instrumentation generateInstrumentationConfiguration(InstrumentationRequest request) {
        def instrumentation = new Instrumentation()
        instrumentation.dataFile = getInstrumentationFilePath(request)
        instrumentation.destination = request.destinationPath + "/instrumentation"
        instrumentation.ignores = request.ignores
        instrumentation.includeClasses = request.includeClasses
        instrumentation.excludeClasses = request.excludeClasses
        instrumentation.ignoreMethodAnnotations = request.ignoreMethodsAnnotations
        instrumentation.ignoreTrivial = request.ignoreTrivial
        instrumentation.auxClassPath = request.auxClasspath
        instrumentation.classes = request.instrumentationClasses
        instrumentation
    }
    /**
     * Returns the file path for the instrumentation file given the request.
     * @param request is the request object.
     * @return the instrumentation file path.
     */
    private static String getInstrumentationFilePath(InstrumentationRequest request) {
        request.destinationPath + "/cobertura.ser"
    }
}
