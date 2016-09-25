package com.psa.coverage.plugin.tasks

import com.psa.coverage.plugin.extensions.ReportingExtension
import com.psa.coverage.reporting.ReportingRequest
import com.psa.coverage.wiring.Controller
import com.psa.coverage.wiring.Presenter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * This task generates the coverage reports.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ReportingTask extends DefaultTask {
    @Input
    String destinationPath
    @Input
    List<String> dataFiles
    @Input
    List<String> sources

    ReportingExtension reportingExtension

    @Input
    def getFormats() {
        reportingExtension.formats
    }

    @Input
    def getCharset() {
        reportingExtension.charset
    }

    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    @TaskAction
    def report() {
        project.logger.info("Starting reporting...")
        def output = new Presenter()
        def request = new ReportingRequest(
                destinationPath: destinationPath,
                dataFiles: dataFiles,
                sources: sources,
                formats: formats == null || formats.isEmpty()?["html"]:formats,
                charset: charset
        )
        new Controller().reporting.generateReports(request, output)
        project.logger.info(output.viewModel.message)
    }
}
