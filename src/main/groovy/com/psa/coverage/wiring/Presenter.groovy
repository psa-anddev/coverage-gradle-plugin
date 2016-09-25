package com.psa.coverage.wiring

import com.psa.coverage.checking.CoverageCheckingOutput
import com.psa.coverage.checking.CoverageCheckingResponse
import com.psa.coverage.instrumentation.InstrumentationOutput
import com.psa.coverage.instrumentation.InstrumentationResponse
import com.psa.coverage.reporting.ReportingOutput
import com.psa.coverage.reporting.ReportingResponse
import com.psa.coverage.viewmodels.ViewModel

/**
 * This is the default output boundary for all use cases in the plug in.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class Presenter implements InstrumentationOutput<ViewModel>, ReportingOutput<ViewModel>,
        CoverageCheckingOutput<ViewModel> {
    private ViewModel viewModel = new ViewModel();

    void processResponse(InstrumentationResponse response) {
        viewModel.message =
                "Instrumentation was successful in the path ${response.instrumentationFilePath}"
    }

    void processResponse(ReportingResponse response) {
        viewModel.message =
                "Reporting was successfully generated in the path ${response.destinationPath}"
    }

    void processResponse(CoverageCheckingResponse response) {
        viewModel.message = "Result of the coverage checking: ${response.message}"
    }

    ViewModel getViewModel() {
        return viewModel
    }
}
