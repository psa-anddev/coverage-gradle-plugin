package com.psa.coverage.plugin.tasks

import com.psa.coverage.instrumentation.InstrumentationRequest
import com.psa.coverage.plugin.extensions.InstrumentationExtension
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * This task will call the instrumentation use case to generate the instrumentation classes
 * and the metadata for the code coverage analysis.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class InstrumentationTask extends DefaultTask {
    @Input
    String destinationPath
    @Input
    List<String> instrumentationClasses

    InstrumentationExtension instrumentationSettings

    /**
     * Gets the list of lines of code to be ignored during instrumentation.
     * @return a list containing all the ignored lines of code.
     */
    @Input
    def getIgnores() {
        instrumentationSettings.ignores
    }

    /**
     * Get the list of classes to be included in the coverage.
     * @return a list of classes to be included in the coverage.
     */
    @Input
    def getIncludeClasses() {
        instrumentationSettings.includeClasses
    }
    /**
     * Get the list of classes to be excluded from those that were initially included.
     * @return a list of classes to be excluded.
     */
    @Input
    def getExcludeClasses() {
        instrumentationSettings.excludeClasses
    }
    /**
     * Gets a list of method annotations to ignore during the code coverage.
     * @return a list of method annotations to ignore.
     */
    @Input
    def getIgnoreMethodAnnotations() {
        instrumentationSettings.ignoreMethodAnnotations
    }
    /**
     * Returns whether to ignore trivial code or not.
     * @return true when trivial code should be ignored.
     */
    @Input
    def getIgnoreTrivial() {
        instrumentationSettings.ignoreTrivial
    }
    /**
     * Returns the auxiliary class path to use.
     * @return auxiliary class path.
     */
    @Input
    def getAuxClasspath() {
        instrumentationSettings.auxClassPath
    }

    @SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
    @TaskAction
    def instrument() {
        project.logger.info("Starting instrumentation...")
        Controller controller = new Controller()
        Presenter presenter = new Presenter()
        InstrumentationRequest request = new InstrumentationRequest(
                destinationPath: destinationPath,
                instrumentationClasses: instrumentationClasses,
                ignores: ignores,
                includeClasses: includeClasses,
                excludeClasses: excludeClasses,
                ignoreMethodsAnnotations: ignoreMethodAnnotations,
                ignoreTrivial: ignoreTrivial,
                auxClasspath: auxClasspath
        )
        def instrumentation = controller.instrumentation
        instrumentation.instrument(request, presenter)
        project.logger.info(presenter.viewModel.message)
    }
}
