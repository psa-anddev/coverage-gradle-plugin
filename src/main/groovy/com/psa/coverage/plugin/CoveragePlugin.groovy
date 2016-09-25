package com.psa.coverage.plugin

import com.psa.coverage.gateways.CoberturaCoverageGateway
import com.psa.coverage.helpers.CoverageContext
import com.psa.coverage.plugin.extensions.CoverageExtension
import com.psa.coverage.plugin.tasks.CoverageCheckingTask
import com.psa.coverage.plugin.tasks.InstrumentationTask
import com.psa.coverage.plugin.tasks.ReportingTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.testing.Test

/**
 * This class applies the coverage plugin.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoveragePlugin implements Plugin<Project> {
    void apply(Project project) {
        /*
        Extension is created to configure the plug in.
         */
        project.logger.info("Creating coverage extension...")
        CoverageExtension extension = project.extensions.create("coverage", CoverageExtension)

        project.afterEvaluate {
            project.logger.info("Configuring coverage extension...")
            if (extension.instrumentation.auxClassPath == null) {
                if (isAndroidApplication(project)) {
                    extension.instrumentation.auxClassPath = []
                }
                else if (isAndroidLibrary(project)) {
                    extension.instrumentation.auxClassPath = []
                }
                else {
                    FileCollection auxiliaryClassPath = project.files project.sourceSets.main.output.classesDir
                    auxiliaryClassPath = auxiliaryClassPath + (project.sourceSets.main.compileClasspath as FileCollection)
                    extension.instrumentation.auxClassPath = auxiliaryClassPath.getAsPath()
                }
            }
        }

        //Configures the right gateway. Since only Cobertura is available at the moment, it can be done upon initialization.
        project.logger.info("Wiring classes...")
        CoverageContext.coverageGateway = new CoberturaCoverageGateway()
        CoverageContext.reportFileName = "cobertura.ser"

        //Set a configuration to add the necessary dependencies for the instrumentation
        project.logger.info("Creating coverage configuration...")
        if (!project.configurations.asMap['coverage']) {
            project.configurations.create('coverage')

            project.afterEvaluate {
                project.logger.info("Configuring coverage dependencies...")
                project.dependencies {
                    coverage('net.sourceforge.cobertura:cobertura:2.1.1')
                }
            }
        }

        if (isAndroidApplication(project)) {
            project.android.applicationVariants.all {
                variant ->
                    createInstrumentationTask(project, variant.name as String)
                    createReportingTask(project, variant.name)
                    createCoveringCheckTask(project, variant)
            }

            project.afterEvaluate {
                project.android.applicationVariants.all {
                    variant ->
                        configureInstrumentation(project, variant, extension)
                        configureReporting(project, variant, extension)
                        configureCoverageChecking(project, variant, extension)
                }
            }
        }
        else if (isAndroidLibrary(project)) {
            project.android.libraryVariants.all {
                variant ->
                    createInstrumentationTask(project, variant.name as String)
                    createReportingTask(project, variant.name)
                    createCoveringCheckTask(project, variant)
            }

            project.afterEvaluate {
                project.android.libraryVariants.all {
                    variant ->
                        configureInstrumentation(project, variant, extension)
                        configureReporting(project, variant, extension)
                        configureCoverageChecking(project, variant, extension)
                }
            }
        }
        else {
            createInstrumentationTask(project, null)
            createReportingTask(project, null)
            createCoveringCheckTask(project, null)
            project.afterEvaluate {
                configureInstrumentation(project, null, extension)
                configureReporting(project, null, extension)
                configureCoverageChecking(project, null, extension)
            }

        }

        project.gradle.taskGraph.whenReady {
            graph ->
                if (isAndroidApplication(project)) {
                    project.android.applicationVariants.all {
                        variant ->
                            if (graph.hasTask(project.tasks.findByName("instrument${variant.name.capitalize()}"))) {
                                project.tasks.withType(Test).matching {
                                    it.name == ("test${variant.name.capitalize()}UnitTest" as String)
                                }.all {
                                    Test task -> wireInstrumentationTask(project, "instrument${variant.name.capitalize()}", task, variant)
                                }
                            }
                    }
                }
                else if (isAndroidLibrary(project)) {
                    project.android.libraryVariants.all {
                        variant ->
                            if (graph.hasTask(project.tasks.findByName("instrument${variant.name.capitalize()}"))) {
                                project.tasks.withType(Test).matching {
                                    it.name == ("test${variant.name.capitalize()}UnitTest" as String)
                                }.all {
                                    Test task -> wireInstrumentationTask(project, "instrument${variant.name.capitalize()}", task, variant)
                                }
                            }
                    }
                }
                else if (graph.hasTask(project.tasks.findByName("instrument"))) {
                    project.tasks.withType(Test).all {
                        Test task ->
                            wireInstrumentationTask(project, "instrument", task, null)
                    }
                }
        }
    }

    private static void wireInstrumentationTask(Project project, String instrumentTaskName, Test task, variant) {
        InstrumentationTask instrumentTask = project.tasks.findByName(instrumentTaskName) as InstrumentationTask
        task.dependsOn instrumentTask
        Configuration configuration = task.project.configurations['coverage']
        task.classpath += configuration
        task.outputs.upToDateWhen { false }
        if (variant == null) {
            task.systemProperties.put('net.sourceforge.cobertura.datafile', project.file("${project.buildDir}/coverage/${CoverageContext.reportFileName}"))
            task.classpath = project.files("${project.buildDir}/coverage/instrumentation") + task.classpath
        }
        else {
            task.systemProperties.put('net.sourceforge.cobertura.datafile', project.file("${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"))
            task.classpath = project.files("${project.buildDir}/coverage/${variant.name}/instrumentation") + task.classpath
        }
    }

    private static void configureCoverageChecking(Project project, variant, CoverageExtension extension) {
        def taskName = "coverageCheck"
        if (variant != null)
            taskName = "${taskName}${variant.name.capitalize()}"
        CoverageCheckingTask coverageChecking = project.tasks.findByName(taskName) as CoverageCheckingTask
        coverageChecking.checkingExtension = extension.coverageChecking
    }

    private CoverageCheckingTask createCoveringCheckTask(Project project, variant) {
        project.logger.info("Creating coverage check task...")
        def taskName = "coverageCheck"
        if (variant != null)
            taskName = "${taskName}${variant.name.capitalize()}"
        CoverageCheckingTask coverageChecking = project.tasks.create(name: taskName, type: CoverageCheckingTask, {
            def dataFilePath
            if (variant == null)
                dataFilePath = "${project.buildDir.path}/coverage/${CoverageContext.reportFileName}"
            else
                dataFilePath = "${project.buildDir.path}/coverage/${variant.name}/${CoverageContext.reportFileName}"

            dataFile = dataFilePath
        }, description: "Performs coverage check") as CoverageCheckingTask
        coverageChecking
    }

    private static void configureReporting(Project project, variant, CoverageExtension extension) {
        def reportingTaskName = "report"
        if (variant != null)
            reportingTaskName = "${reportingTaskName}${variant.name.capitalize()}"
        ReportingTask reportingTask = project.tasks.findByName(reportingTaskName) as ReportingTask
        reportingTask.reportingExtension = extension.reporting
        if (isAndroidApplication(project) || isAndroidLibrary(project)) {
            reportingTask.sources = []
            variant.getSourceSets().each {
                sourceSet -> reportingTask.sources += sourceSet.java.srcDirs
            }
        }
        else {
            reportingTask.sources = project.sourceSets.main.java.srcDirs as List<String>
            if (project.sourceSets.main.hasProperty('kotlin'))
                reportingTask.sources += project.sourceSets.main.kotlin.srcDirs
        }
        if (variant == null)
            reportingTask.dataFiles = ["${project.buildDir}/coverage/${CoverageContext.reportFileName}"]
        else {
            reportingTask.dataFiles = ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"]
        }

    }

    private ReportingTask createReportingTask(Project project, variantName) {
        project.logger.info("Creating reporting task...")
        def taskName = "report"
        if (variantName != null && !variantName.isEmpty())
            taskName = "${taskName}${variantName.capitalize()}"
        ReportingTask reportingTask = project.tasks.create(name: "$taskName", type: ReportingTask,
                {
                    if (variantName == null || variantName.isEmpty())
                        destinationPath = "${project.buildDir.path}/coverage"
                    else
                        destinationPath = "${project.buildDir.path}/coverage/${variantName}"
                }, description: "Generates coverage reports") as ReportingTask
        reportingTask
    }

    private static boolean isAndroidApplication(Project project) {
        project.plugins.hasPlugin("com.android.application")
    }

    private static boolean isAndroidLibrary(Project project) {
        project.plugins.hasPlugin("com.android.library")
    }

    private InstrumentationTask createInstrumentationTask(Project project, String variantName) {
        project.logger.info("Creating instrumentation task...")
        InstrumentationTask instrumentTask
        if (variantName == null || variantName.isEmpty())
            instrumentTask = project.tasks.create(name: "instrument", type: InstrumentationTask, {
                destinationPath = "${project.buildDir.path}/coverage"
            }, description: "Instrument the classes") as InstrumentationTask
        else
            instrumentTask = project.tasks.create(name: "instrument${variantName.capitalize()}", type: InstrumentationTask, {
                destinationPath = "${project.buildDir.path}/coverage/${variantName}"
            }, description: "Instrument the classes") as InstrumentationTask

        instrumentTask
    }

    private static configureInstrumentation(Project project, variant, CoverageExtension extension) {
        project.logger.info("Configuring instrumentation...")
        List<String> instrumentationClasses
        if (isAndroidApplication(project) || isAndroidLibrary(project)) {
            instrumentationClasses = (["${project.buildDir}/intermediates/classes/${variant.dirName}"] as List<String>)
        }
        else {
            instrumentationClasses = [project.sourceSets.main.output.classesDir.path] as List<String>
        }
        InstrumentationTask instrumentationTask
        if (variant == null)
            instrumentationTask = project.tasks.findByName("instrument") as InstrumentationTask
        else {
            instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
            FileCollection auxiliaryClasspath = project.files("${project.buildDir}/intermediates/classes/$variant.name")
            auxiliaryClasspath = auxiliaryClasspath + project.files(project.configurations.getByName("compile"),
                    project.configurations.getByName("${variant.name}Compile"))
            extension.instrumentation.auxClassPath = auxiliaryClasspath
        }

        instrumentationTask.instrumentationSettings = extension.instrumentation
        instrumentationTask.instrumentationClasses = instrumentationClasses
        project.tasks.matching {
            def dependentTaskName
            if (variant == null)
                dependentTaskName = 'classes'
            else
                dependentTaskName = "compile${variant.name.capitalize()}JavaWithJavac"

            it.name == dependentTaskName
        }.all {
            task ->
                project.logger.info("Making :${project.name}:instrument depend on :${task.project.name}:${task.name}")
                instrumentationTask.dependsOn task
        }
    }
}
