package com.psa.coverage.wiring

import com.psa.coverage.checking.CoverageCheckingInput
import com.psa.coverage.checking.CoverageCheckingInteractor
import com.psa.coverage.instrumentation.InstrumentationInput
import com.psa.coverage.instrumentation.InstrumentationInteractor
import com.psa.coverage.reporting.ReportingInput
import com.psa.coverage.reporting.ReportingInteractor

/**
 * This class provides the different use cases for the plug in.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class Controller {
    /**
     * Gets the default input boundary for the instrumentation use case.
     * @return the default input boundary for instrumentation.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    public InstrumentationInput getInstrumentation() {
        new InstrumentationInteractor()
    }

    /**
     * Gets the default input boundary for the reporting use case.
     * @return the default input boundary for reporting
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    public ReportingInput getReporting() {
        new ReportingInteractor()
    }

    /**
     * Gets the default input boundary for the coverage checking use case.
     * @return the default input boundary for coverage checking.
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    public CoverageCheckingInput getCheckCoverage() {
        new CoverageCheckingInteractor()
    }
}
