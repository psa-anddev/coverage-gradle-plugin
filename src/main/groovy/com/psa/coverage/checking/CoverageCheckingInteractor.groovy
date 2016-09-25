package com.psa.coverage.checking

import com.psa.coverage.configuration.CoverageChecking
import com.psa.coverage.helpers.CoverageContext
import com.psa.coverage.helpers.Rate
import com.psa.coverage.helpers.RegexRate

/**
 * This is the interactor for the coverage checking use case.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageCheckingInteractor implements CoverageCheckingInput {
    void checkCoverage(CoverageCheckingRequest request, CoverageCheckingOutput output) {
        CoverageContext.coverageGateway.check(new CoverageChecking(
                dataFile: request.dataFile,
                classRate: new Rate(line: request.classLineRate, branch: request.classBranchRate),
                packageRate: new Rate(line: request.packageLineRate,
                        branch: request.packageBranchRate),
                totalRate: new Rate(line: request.totalLineRate, branch: request.totalBranchRate),
                regexRateList: getRegularExpressions(request)
        ))
        output.processResponse(new CoverageCheckingResponse(message: "Success."))
    }

    private static List<RegexRate> getRegularExpressions(CoverageCheckingRequest coverageCheckingRequest) {
        List<RegexRate> regexRateList = new ArrayList<>(coverageCheckingRequest.regexElements.size())

        coverageCheckingRequest.regexElements.forEach({
            regexRateList.add(new RegexRate(
                    regex: it.regex,
                    rate: new Rate(line: it.line, branch: it.branch)
            ))
        })

        regexRateList
    }
}
