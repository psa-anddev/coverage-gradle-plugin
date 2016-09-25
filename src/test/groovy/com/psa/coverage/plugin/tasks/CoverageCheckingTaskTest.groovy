package com.psa.coverage.plugin.tasks

import com.psa.coverage.checking.CoverageCheckingInput
import com.psa.coverage.plugin.extensions.CheckingExtension
import com.psa.coverage.plugin.extensions.RateExtension
import com.psa.coverage.plugin.extensions.RegexRateExtension
import com.psa.coverage.viewmodels.ViewModel
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Feature: As a developer, I want to be able to check that my coverage is above certain
 * standards so that I can ship my code.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageCheckingTaskTest extends Specification {

    @SuppressWarnings(["GroovyAssignabilityCheck", "GrUnresolvedAccess"])
    def "Coverage checking task executes as expected."() {
        def anyOutput = GroovySpy(Presenter, global: true)
        def anyController = GroovySpy(Controller, global: true)
        def coverageChecking = GroovyMock(CoverageCheckingInput)
        def viewModel = GroovyMock(ViewModel)

        given: "I have applied the coverage plugin"
        Project project = ProjectBuilder.builder().build()
        def task = project.task("coverageCheck", type: CoverageCheckingTask) as CoverageCheckingTask
        task.dataFile = "/some/data/file"

        and: "I have set up my coverage standards"
        def checkingExtension = new CheckingExtension(
                classRate: new RateExtension(
                        line: 26,
                        branch: 10
                ),
                packageRate: new RateExtension(
                        line: 12,
                        branch: 11
                ),
                totalRate: new RateExtension(
                        line: 26,
                        branch: 1
                ),
                regexRates: [
                        new RegexRateExtension(
                                regex: "*.M",
                                rate: new RateExtension(
                                        line: 26,
                                        branch: 10
                                )
                        ),
                        new RegexRateExtension(
                                regex: "*.G",
                                rate: new RateExtension(
                                        line: 1,
                                        branch: 85
                                )
                        )
                ]
        )
        task.checkingExtension = checkingExtension

        when: "Gradle executes the task"
        task.execute()

        then: "the necessary classes are instantiated"
        1 * new Presenter() >> anyOutput
        1 * new Controller() >> anyController


        and: "the use case is executed"
        1 * anyController.checkCoverage >> coverageChecking
        1 * coverageChecking.checkCoverage({
            it.dataFile == task.dataFile &&
                    it.classLineRate == task.checkingExtension.classRate.line &&
                    it.classBranchRate == task.checkingExtension.classRate.branch &&
                    it.packageLineRate == task.checkingExtension.packageRate.line &&
                    it.packageBranchRate == task.checkingExtension.packageRate.branch &&
                    it.totalLineRate == task.checkingExtension.totalRate.line &&
                    it.totalBranchRate == task.checkingExtension.totalRate.branch &&
                    it.regexElements[0].regex == "*.M" &&
                    it.regexElements[0].line == 26 && it.regexElements[0].branch == 10 &&
                    it.regexElements[1].regex == "*.G" &&
                    it.regexElements[1].line == 1 && it.regexElements[1].branch == 85
        }, anyOutput)

        and: "the result printed to the log"
        1 * anyOutput.viewModel >> viewModel
        1 * viewModel.message
    }
}
