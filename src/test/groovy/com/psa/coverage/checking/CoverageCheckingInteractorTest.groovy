package com.psa.coverage.checking

import com.psa.coverage.configuration.CoverageChecking
import com.psa.coverage.gateways.CoverageGateway
import com.psa.coverage.helpers.CoverageContext
import spock.lang.*

/**
 * Test for the coverage checking interactor.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageCheckingInteractorTest extends Specification {

    def "Check coverage"() {
        def gateway = GroovyMock(CoverageGateway)
        def output = GroovyMock(CoverageCheckingOutput)
        CoverageContext.coverageGateway = gateway

        given: "I have a check coverage request"
        CoverageCheckingRequest request = new CoverageCheckingRequest(
                dataFile: "/some/data/file",
                classLineRate: 26,
                classBranchRate: 1,
                packageBranchRate: 10,
                packageLineRate: 1,
                totalLineRate: 85,
                totalBranchRate: 13,
                regexElements: [
                        new RegexElement(regex: ".*", line: 26, branch: 1),
                        new RegexElement(regex: ".*mg", line: 85, branch: 10)
                ]
        )

        when: "I check my coverage"
        CoverageCheckingInteractor interactor = new CoverageCheckingInteractor()
        interactor.checkCoverage(request, output)

        then: "Coverage is checked"
        1 * gateway.check({
            it.dataFile == request.dataFile && it.classRate != null &&
                    it.classRate.line == request.classLineRate &&
                    it.classRate.branch == request.classBranchRate && it.packageRate != null &&
                    it.packageRate.line == request.packageLineRate &&
                    it.packageRate.branch == request.packageBranchRate && it.totalRate != null &&
                    it.totalRate.line == request.totalLineRate &&
                    it.totalRate.branch == request.totalBranchRate && it.regexRateList != null &&
                    it.regexRateList[0].regex == request.regexElements[0].regex &&
                    it.regexRateList[0].rate.line == request.regexElements[0].line &&
                    it.regexRateList[0].rate.branch == request.regexElements[0].branch &&
                    it.regexRateList[1].regex == request.regexElements[1].regex &&
                    it.regexRateList[1].rate.line == request.regexElements[1].line &&
                    it.regexRateList[1].rate.branch == request.regexElements[1].branch
        } as CoverageChecking)

        and: "The response is delivered"
        1 * output.processResponse({
            it.message == "Success."
        } as CoverageCheckingResponse)
    }
}
