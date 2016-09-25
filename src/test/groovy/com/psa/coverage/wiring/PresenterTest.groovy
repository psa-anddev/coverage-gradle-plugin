package com.psa.coverage.wiring

import com.psa.coverage.checking.CoverageCheckingResponse
import com.psa.coverage.instrumentation.InstrumentationResponse
import com.psa.coverage.reporting.ReportingResponse
import spock.lang.*

/**
 * Feature: As a user, I want to have a visual representation of the results given by the
 * plugin so that I can know if the operations were successful.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class PresenterTest extends Specification {
    def "Instrumentation result is converted into the result output"() {
        given: "I got an instrumentation response"
        InstrumentationResponse response =
                new InstrumentationResponse(instrumentationFilePath: "/some/instrumentation/path")

        when: "the presenter generates the view model"
        Presenter presenter = new Presenter()
        presenter.processResponse(response)

        then: "I can get the text to be printed"
        presenter.viewModel.message == "Instrumentation was successful in the path ${response.instrumentationFilePath}"
    }

    def "Reporting request is converted into the right view model"() {
        given: "I got a reporting response"
        ReportingResponse response =
                new ReportingResponse(destinationPath: "/some/destination/path")

        when: "the presenter generates the view model"
        Presenter presenter = new Presenter()
        presenter.processResponse(response)

        then: "I can get the text to be printed"
        presenter.viewModel.message == "Reporting was successfully generated in the path ${response.destinationPath}"
    }

    def "Coverage checking returns the right message."() {
        given: "I got a coverage checking response"
        CoverageCheckingResponse response = new CoverageCheckingResponse(message: "Message")

        when: "the presenter generates the view model"
        Presenter presenter = new Presenter()
        presenter.processResponse(response)

        then: "I can get the text to be printed"
        presenter.viewModel.message == "Result of the coverage checking: ${response.message}"
    }
}
