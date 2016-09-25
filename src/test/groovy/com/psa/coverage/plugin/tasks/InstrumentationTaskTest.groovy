package com.psa.coverage.plugin.tasks

import com.psa.coverage.instrumentation.InstrumentationInput
import com.psa.coverage.plugin.extensions.InstrumentationExtension
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Feature: As a user, I want to have an instrumentation tastk so that I can
 * measure the code coverage of my application.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class InstrumentationTaskTest extends Specification {
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "Instrumentation task instrument the classes."() {
        def anyController = GroovySpy(Controller, global:true)
        def instrumentation = GroovyMock(InstrumentationInput)
        def instrumentationOutput = GroovySpy(Presenter, global: true)
        given: "I have an instance of the instrumentation task"
        Project project = ProjectBuilder.builder().build()
        def task = project.task('instrumentation', type: InstrumentationTask) as InstrumentationTask

        when: "Gradle executes the task"
        def instrumentationExtension = new InstrumentationExtension()
        instrumentationExtension.ignores = ["*a"]
        instrumentationExtension.includeClasses = ["*.com.psa.coverage.*"]
        instrumentationExtension.excludeClasses = ["*.R.*"]
        instrumentationExtension.ignoreMethodAnnotations = ["BindViews"]
        instrumentationExtension.ignoreTrivial = true
        instrumentationExtension.auxClassPath = "/some/class/path"

        task.destinationPath = "/some/destination/path"
        task.instrumentationClasses = ["Class1", "Class2"]
        task.instrumentationSettings = instrumentationExtension
        task.execute()

        then: "the instrumentation use case is executed."
        1 * new Controller()
        1 * anyController.instrumentation >> instrumentation
        1 * new Presenter() >> instrumentationOutput
        1 * instrumentation.instrument({
            it.destinationPath == "/some/destination/path" &&
                    it.instrumentationClasses == task.instrumentationClasses &&
                    it.ignores == task.instrumentationSettings.ignores &&
                    it.includeClasses == task.instrumentationSettings.includeClasses &&
                    it.excludeClasses == task.instrumentationSettings.excludeClasses &&
                    it.ignoreMethodsAnnotations == task.instrumentationSettings.ignoreMethodAnnotations &&
                    it.ignoreTrivial == task.instrumentationSettings.ignoreTrivial &&
                    it.auxClasspath == task.instrumentationSettings.auxClassPath
        }, instrumentationOutput)
        1 * instrumentationOutput.viewModel
    }
}
