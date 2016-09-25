package com.psa.coverage.reporting

import com.psa.coverage.configuration.Reporting
import com.psa.coverage.gateways.CoverageGateway
import com.psa.coverage.helpers.CoverageContext
import spock.lang.*

import java.nio.charset.Charset

/**
 * Tests for the reporting interactor
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class ReportingInteractorTest extends Specification {

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Instrumentation with one metadata file"() {
        def gateway = GroovyMock(CoverageGateway)
        CoverageContext.coverageGateway = gateway
        def output = GroovyMock(ReportingOutput)

        given: "I have a reporting request with one metadata file"
        ReportingRequest request = new ReportingRequest(
                destinationPath: "/some/destination/path", dataFiles: ["/some/data.ser"],
                formats: ["html", "xml"], charset: Charset.defaultCharset(),
                sources: ["/some/sources"]
        )

        when: "I generate the reports"
        ReportingInteractor interactor = new ReportingInteractor()
        interactor.generateReports(request, output)

        then: "No merge reports action is taken"
        0 * gateway.merge(_, *_)

        and: "the reports are generated"
        1 * gateway.generateReport({
            it.dataFile == request.dataFiles[0] &&
                    it.destination == "${request.destinationPath}/reports" &&
                    it.formats == request.formats &&
                    it.encoding == request.charset.toString() &&
                    it.source == request.sources
        } as Reporting)

        and: "the response is delivered"
        1 * output.processResponse({
            it.destinationPath == "${request.destinationPath}/reports"
        } as ReportingResponse)
    }

    def "Generate reports with several data files"() {
        def gateway = GroovyMock(CoverageGateway)
        CoverageContext.coverageGateway = gateway
        def output = GroovyMock(ReportingOutput)

        given: "I have a request with four metadata files"
        def request = new ReportingRequest(
                destinationPath: "/some/destination/path", dataFiles: ["/some/data.ser",
                                                                       "/some/other/data.ser"],
                formats: ["html", "xml"], charset: Charset.defaultCharset(),
                sources: ["/some/sources"]
        )

        when: "I generate the reports"
        ReportingInteractor interactor = new ReportingInteractor()
        interactor.generateReports(request, output)

        then: "The metadata is merged"
        1 * gateway.merge("${request.destinationPath}/merged.ser", request.dataFiles)

        and: "The reports are merged"
        1 * gateway.generateReport({
            it.dataFile == "${request.destinationPath}/merged.ser" &&
                    it.destination == "${request.destinationPath}/reports" &&
                    it.formats == request.formats &&
                    it.encoding == request.charset.toString() &&
                    it.source == request.sources
        } as Reporting)

        and: "The response is delivered"
        1 * output.processResponse({
            it.destinationPath == "${request.destinationPath}/reports"
        } as ReportingResponse)
    }
}
