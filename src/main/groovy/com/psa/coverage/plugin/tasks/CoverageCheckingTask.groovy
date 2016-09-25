package com.psa.coverage.plugin.tasks

import com.psa.coverage.checking.CoverageCheckingRequest
import com.psa.coverage.checking.RegexElement
import com.psa.coverage.plugin.extensions.CheckingExtension
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * This gradle task performs the coverage checking.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class CoverageCheckingTask extends DefaultTask {
    @Input
    String dataFile
    CheckingExtension checkingExtension

    @Input
    def getClassLineRate() {
        checkingExtension.classRate.line
    }

    @Input
    def getClassBranchRate() {
        checkingExtension.classRate.branch
    }

    @Input
    def getPackageLineRate() {
        checkingExtension.packageRate.line
    }

    @Input
    def getPackageBranchRate() {
        checkingExtension.packageRate.branch
    }

    @Input
    def getTotalLineRate() {
        checkingExtension.totalRate.line
    }

    @Input
    def getTotalBranchRate() {
        checkingExtension.totalRate.branch
    }

    @Input
    def getRegularExpressionRates() {
        List<RegexElement> regexElements = []
        checkingExtension.regexRates.forEach({
            regexElements << new RegexElement(
                    regex: it.regex, line: it.rate.line, branch: it.rate.branch)
        })
        regexElements
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    def checkCoverage() {
        project.logger.info("Starting code coverage checking...")
        def presenter = new Presenter()
        def request = new CoverageCheckingRequest(
                dataFile: dataFile,
                classLineRate: classLineRate,
                classBranchRate: classBranchRate,
                packageLineRate: packageLineRate,
                packageBranchRate: packageBranchRate,
                totalLineRate: totalLineRate,
                totalBranchRate: totalBranchRate,
                regexElements: regularExpressionRates
        )
        new Controller().checkCoverage.checkCoverage(request, presenter)
        project.logger.info(presenter.viewModel.message)
    }
}
