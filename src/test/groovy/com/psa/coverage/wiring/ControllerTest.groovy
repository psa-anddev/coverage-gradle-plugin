package com.psa.coverage.wiring

import com.psa.coverage.checking.CoverageCheckingInteractor
import com.psa.coverage.instrumentation.InstrumentationInteractor
import com.psa.coverage.reporting.ReportingInteractor
import spock.lang.*

/**
 * Feature: As a developer, I need a controller so that I can trigger the different
 * use cases of the plug in.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ControllerTest extends Specification {

    def "Getting the instrumentation use case."() {
        given: "I have a controller"
        Controller controller = new Controller()

        when: "I want to execute the instrumentation use case"

        then: "I get the right interactor"
        controller.instrumentation instanceof InstrumentationInteractor
    }

    def "Getting the reporting use case."() {
        given: "I have a controller"
        Controller controller = new Controller()

        when: "I want to execute the reporting use case"

        then: "I get the right interactor"
        controller.reporting instanceof ReportingInteractor
    }

    def "Getting the check coverage use case"() {
        given: "I have a controller"
        Controller controller = new Controller()

        when: "I want to execute the check coverage use case"

        then: "I get the right interactor"
        controller.checkCoverage instanceof CoverageCheckingInteractor
    }
}
