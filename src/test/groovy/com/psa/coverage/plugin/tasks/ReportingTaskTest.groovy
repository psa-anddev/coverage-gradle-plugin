package com.psa.coverage.plugin.tasks

import com.psa.coverage.plugin.extensions.ReportingExtension
import com.psa.coverage.reporting.ReportingInput
import com.psa.coverage.viewmodels.ViewModel
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*

import java.nio.charset.Charset

/**
 * Feature: As a user, I want to have a report of my code coverage so that I can know
 * how well my code is tested.
 *
 * @author Pablo Sánchez Alonso
 */
class ReportingTaskTest extends Specification {
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Reporting task executes as expected"() {
        def anyController = GroovySpy(Controller, global: true)
        def reporting = GroovyMock(ReportingInput)
        def anyPresenter = GroovySpy(Presenter, global: true)
        def viewModel = GroovyMock(ViewModel)

        given: "I have a project configured with the coverage plugin"
        Project project = ProjectBuilder.builder().build()
        def reportingExtension = new ReportingExtension(
                formats: ['html', 'xml'],
                charset: Charset.forName("UTF-8")
        )
        def task = project.task("reporting", type: ReportingTask) as ReportingTask
        task.destinationPath = "/some/destination/path"
        task.dataFiles = ["/some/data/file", "/some/other/data/file"]
        task.sources = ["/some/source", "/some/other/source"]
        task.reportingExtension = reportingExtension

        when: "Gradle executes the task"
        task.execute()

        then: "The reporting use case is called"
        1 * new Presenter() >> anyPresenter
        1 * new Controller() >> anyController
        1 * anyController.reporting >> reporting
        1 * reporting.generateReports({
            it.destinationPath == task.destinationPath &&
                    it.dataFiles == task.dataFiles &&
                    it.sources == task.sources &&
                    it.formats == task.formats &&
                    it.charset == task.charset
        }, anyPresenter)
        1 * anyPresenter.viewModel >> viewModel
        1 * viewModel.message
    }
}
