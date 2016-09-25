package com.psa.coverage.plugin

import com.psa.coverage.gateways.CoberturaCoverageGateway
import com.psa.coverage.helpers.CoverageContext
import com.psa.coverage.plugin.extensions.CoverageExtension
import com.psa.coverage.plugin.tasks.CoverageCheckingTask
import com.psa.coverage.plugin.tasks.InstrumentationTask
import com.psa.coverage.plugin.tasks.ReportingTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Feature: As a user, I want to be able to apply the plugin so that I can
 * check my coverage.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoveragePluginTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder();

    @SuppressWarnings(["GrUnresolvedAccess", "GrEqualsBetweenInconvertibleTypes"])
    def "Apply the plug in in a Java project"() {
        given: "I have a Java project"
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'java'

        when: "I apply the coverage plug in."
        project.apply plugin: 'com.psa.coverage'
        project.coverage {
            instrumentation {
                includeClass ".*"
            }

            reporting {
                format 'html'
                format 'xml'
            }

            coverageChecking {
                classRate {
                    line 26
                }
            }
        }

        project.evaluate()


        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension
        project.coverage.instrumentation.auxClassPath != null

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "A task is created for instrumentation."
        InstrumentationTask instrument = project.tasks.findByName("instrument") as InstrumentationTask
        instrument.destinationPath == "${project.buildDir.path}/coverage"
        instrument.description == "Instrument the classes"
        instrument.instrumentationSettings == project.coverage.instrumentation
        instrument.instrumentationClasses.contains(project.sourceSets.main.output.classesDir.path)

        and: "A reporting task is created."
        ReportingTask report = project.tasks.findByName("report") as ReportingTask
        report != null
        report.destinationPath == "${project.buildDir.path}/coverage"
        report.description == "Generates coverage reports"
        report.reportingExtension == project.coverage.reporting
        report.sources == project.sourceSets.main.java.srcDirs as List<String>
        report.dataFiles == ["${project.buildDir}/coverage/${CoverageContext.reportFileName}"]

        and: "A coverage check task is generated"
        CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck") as CoverageCheckingTask
        coverageCheck != null
        coverageCheck.dataFile == "${project.buildDir}/coverage/${CoverageContext.reportFileName}"
        coverageCheck.description == "Performs coverage check"
        coverageCheck.checkingExtension == project.coverage.coverageChecking
    }

    @SuppressWarnings("GrEqualsBetweenInconvertibleTypes")
    def "Apply Kotlin plugin into project"() {
        given: "I have a Kotlin project"
        Project project = ProjectBuilder.builder().build();
        project.buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.0.3"
            }
        }
        project.apply plugin: "kotlin"

        when: "I apply the coverage plugin"
        project.apply plugin: "com.psa.coverage"

        project.coverage {
            instrumentation {
                includeClass ".*"
            }

            reporting {
                format 'html'
                format 'xml'
            }

            coverageChecking {
                classRate {
                    line 26
                }
            }
        }

        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension
        project.coverage.instrumentation.auxClassPath != null

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "A task is created for instrumentation."
        InstrumentationTask instrument = project.tasks.findByName("instrument") as InstrumentationTask
        instrument.destinationPath == "${project.buildDir.path}/coverage"
        instrument.description == "Instrument the classes"
        instrument.instrumentationSettings == project.coverage.instrumentation
        instrument.instrumentationClasses.contains(project.sourceSets.main.output.classesDir.path)

        and: "A reporting task is created."
        ReportingTask report = project.tasks.findByName("report") as ReportingTask
        report != null
        report.destinationPath == "${project.buildDir.path}/coverage"
        report.description == "Generates coverage reports"
        report.reportingExtension == project.coverage.reporting
        report.sources.containsAll(project.sourceSets.main.kotlin.srcDirs as List<String>)
        report.dataFiles == ["${project.buildDir}/coverage/${CoverageContext.reportFileName}"]

        and: "A coverage check task is generated"
        CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck") as CoverageCheckingTask
        coverageCheck != null
        coverageCheck.dataFile == "${project.buildDir}/coverage/${CoverageContext.reportFileName}"
        coverageCheck.description == "Performs coverage check"
        coverageCheck.checkingExtension == project.coverage.coverageChecking
    }

    def "Apply plugin to Android application project without flavours"() {
        given: "I have an Android Library project without any flavours"
        Project project = ProjectBuilder.builder().build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.application'

        project.android {
            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                applicationId "com.psa.example"
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for the debug build type."
        InstrumentationTask instrumentationDebugTask = project.tasks.findByName("instrumentDebug") as InstrumentationTask
        instrumentationDebugTask != null
        instrumentationDebugTask.destinationPath == "${project.buildDir.path}/coverage/debug" as String
        instrumentationDebugTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationDebugTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/debug")

        and: "An instrumentation task is created for the release build type."
        InstrumentationTask instrumentationReleaseTask = project.tasks.findByName("instrumentRelease") as InstrumentationTask
        instrumentationReleaseTask != null
        instrumentationReleaseTask.destinationPath == "${project.buildDir.path}/coverage/release" as String
        instrumentationReleaseTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationReleaseTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/release")

        and: "A reporting task is created for the debug build type."
        ReportingTask reportDebug = project.tasks.findByName("reportDebug") as ReportingTask
        reportDebug != null
        reportDebug.destinationPath == "${project.buildDir.path}/coverage/debug" as String
        reportDebug.description == "Generates coverage reports"
        reportDebug.reportingExtension == project.coverage.reporting
        reportDebug.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportDebug.sources.containsAll(project.android.sourceSets.debug.java.srcDirs as List<String>)
        reportDebug.dataFiles == ["${project.buildDir}/coverage/debug/${CoverageContext.reportFileName}"] as List<String>

        and: "A reporting task is created for the release build type."
        ReportingTask reportRelease = project.tasks.findByName("reportRelease") as ReportingTask
        reportRelease != null
        reportRelease.destinationPath == "${project.buildDir.path}/coverage/release" as String
        reportRelease.description == "Generates coverage reports"
        reportRelease.reportingExtension == project.coverage.reporting
        reportRelease.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportRelease.sources.containsAll(project.android.sourceSets.release.java.srcDirs as List<String>)
        reportRelease.dataFiles == ["${project.buildDir}/coverage/release/${CoverageContext.reportFileName}"] as List<String>

        and: "A coverage check task is generated for the debug build type"
        CoverageCheckingTask coverageCheckDebug = project.tasks.findByName("coverageCheckDebug") as CoverageCheckingTask
        coverageCheckDebug != null
        coverageCheckDebug.dataFile == "${project.buildDir}/coverage/debug/${CoverageContext.reportFileName}" as String
        coverageCheckDebug.description == "Performs coverage check"
        coverageCheckDebug.checkingExtension == project.coverage.coverageChecking

        and: "A coverage check task is generated for the release build type"
        CoverageCheckingTask coverageCheckRelease = project.tasks.findByName("coverageCheckRelease") as CoverageCheckingTask
        coverageCheckRelease != null
        coverageCheckRelease.dataFile == "${project.buildDir}/coverage/release/${CoverageContext.reportFileName}" as String
        coverageCheckRelease.description == "Performs coverage check"
        coverageCheckRelease.checkingExtension == project.coverage.coverageChecking
    }

    def "Apply Coverage plug in to an Android library project without flavours"() {
        given: "I have an Android library project"
        File manifest = new File (testProjectDir.newFolder("src", "main"), "AndroidManifest.xml")
        manifest << """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  package="com.psa.example">

         </manifest>
        """
        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.library'

        project.android {
            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for the debug build type."
        InstrumentationTask instrumentationDebugTask = project.tasks.findByName("instrumentDebug") as InstrumentationTask
        instrumentationDebugTask != null
        instrumentationDebugTask.destinationPath == "${project.buildDir.path}/coverage/debug" as String
        instrumentationDebugTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationDebugTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/debug")

        and: "An instrumentation task is created for the release build type."
        InstrumentationTask instrumentationReleaseTask = project.tasks.findByName("instrumentRelease") as InstrumentationTask
        instrumentationReleaseTask != null
        instrumentationReleaseTask.destinationPath == "${project.buildDir.path}/coverage/release" as String
        instrumentationReleaseTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationReleaseTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/release")

        and: "A reporting task is created for the debug build type."
        ReportingTask reportDebug = project.tasks.findByName("reportDebug") as ReportingTask
        reportDebug != null
        reportDebug.destinationPath == "${project.buildDir.path}/coverage/debug" as String
        reportDebug.description == "Generates coverage reports"
        reportDebug.reportingExtension == project.coverage.reporting
        reportDebug.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportDebug.sources.containsAll(project.android.sourceSets.debug.java.srcDirs as List<String>)
        reportDebug.dataFiles == ["${project.buildDir}/coverage/debug/${CoverageContext.reportFileName}"] as List<String>

        and: "A reporting task is created for the release build type."
        ReportingTask reportRelease = project.tasks.findByName("reportRelease") as ReportingTask
        reportRelease != null
        reportRelease.destinationPath == "${project.buildDir.path}/coverage/release" as String
        reportRelease.description == "Generates coverage reports"
        reportRelease.reportingExtension == project.coverage.reporting
        reportRelease.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportRelease.sources.containsAll(project.android.sourceSets.release.java.srcDirs as List<String>)
        reportRelease.dataFiles == ["${project.buildDir}/coverage/release/${CoverageContext.reportFileName}"] as List<String>

        and: "A coverage check task is generated for the debug build type"
        CoverageCheckingTask coverageCheckDebug = project.tasks.findByName("coverageCheckDebug") as CoverageCheckingTask
        coverageCheckDebug != null
        coverageCheckDebug.dataFile == "${project.buildDir}/coverage/debug/${CoverageContext.reportFileName}" as String
        coverageCheckDebug.description == "Performs coverage check"
        coverageCheckDebug.checkingExtension == project.coverage.coverageChecking

        and: "A coverage check task is generated for the release build type"
        CoverageCheckingTask coverageCheckRelease = project.tasks.findByName("coverageCheckRelease") as CoverageCheckingTask
        coverageCheckRelease != null
        coverageCheckRelease.dataFile == "${project.buildDir}/coverage/release/${CoverageContext.reportFileName}" as String
        coverageCheckRelease.description == "Performs coverage check"
        coverageCheckRelease.checkingExtension == project.coverage.coverageChecking
    }

    def "Android application with product flavours but not dimensions"() {
        given: "I have an Android application with two flavours"
        Project project = ProjectBuilder.builder().build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.application'

        project.android {
            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                applicationId "com.psa.example"
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                demo {
                    applicationId "com.psa.demo"
                }

                full {
                    applicationId "com.psa.full"
                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for the demo debug variant."
        InstrumentationTask instrumentationDemoDebugTask = project.tasks.findByName("instrumentDemoDebug") as InstrumentationTask
        instrumentationDemoDebugTask != null
        instrumentationDemoDebugTask.destinationPath == "${project.buildDir.path}/coverage/demoDebug" as String
        instrumentationDemoDebugTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationDemoDebugTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/demo/debug")

        and: "An instrumentation task is created for the full debug variant."
        InstrumentationTask instrumentationFullDebugTask = project.tasks.findByName("instrumentFullDebug") as InstrumentationTask
        instrumentationFullDebugTask != null
        instrumentationFullDebugTask.destinationPath == "${project.buildDir.path}/coverage/fullDebug" as String
        instrumentationFullDebugTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationFullDebugTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/full/debug")

        and: "An instrumentation task is created for the demo release variant."
        InstrumentationTask instrumentationDemoReleaseTask = project.tasks.findByName("instrumentDemoRelease") as InstrumentationTask
        instrumentationDemoReleaseTask != null
        instrumentationDemoReleaseTask.destinationPath == "${project.buildDir.path}/coverage/demoRelease" as String
        instrumentationDemoReleaseTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationDemoReleaseTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/demo/release")

        and: "An instrumentation task is created for the full release variant."
        InstrumentationTask instrumentationFullReleaseTask = project.tasks.findByName("instrumentFullRelease") as InstrumentationTask
        instrumentationFullReleaseTask != null
        instrumentationFullReleaseTask.destinationPath == "${project.buildDir.path}/coverage/fullRelease" as String
        instrumentationFullReleaseTask.instrumentationSettings == project.coverage.instrumentation
        instrumentationFullReleaseTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/full/release")

        and: "A reporting task is created for the demo debug variant."
        ReportingTask reportDemoDebug = project.tasks.findByName("reportDemoDebug") as ReportingTask
        reportDemoDebug != null
        reportDemoDebug.destinationPath == "${project.buildDir.path}/coverage/demoDebug" as String
        reportDemoDebug.description == "Generates coverage reports"
        reportDemoDebug.reportingExtension == project.coverage.reporting
        reportDemoDebug.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportDemoDebug.sources.containsAll(project.android.sourceSets.debug.java.srcDirs as List<String>)
        reportDemoDebug.sources.containsAll(project.android.sourceSets.demo.java.srcDirs as List<String>)
        reportDemoDebug.sources.containsAll(project.android.sourceSets.demoDebug.java.srcDirs as List<String>)
        reportDemoDebug.dataFiles == ["${project.buildDir}/coverage/demoDebug/${CoverageContext.reportFileName}"] as List<String>

        and: "A reporting task is created for the demo release variant."
        ReportingTask reportDemoRelease = project.tasks.findByName("reportDemoRelease") as ReportingTask
        reportDemoRelease != null
        reportDemoRelease.destinationPath == "${project.buildDir.path}/coverage/demoRelease" as String
        reportDemoRelease.description == "Generates coverage reports"
        reportDemoRelease.reportingExtension == project.coverage.reporting
        reportDemoRelease.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportDemoRelease.sources.containsAll(project.android.sourceSets.release.java.srcDirs as List<String>)
        reportDemoRelease.sources.containsAll(project.android.sourceSets.demo.java.srcDirs as List<String>)
        reportDemoRelease.sources.containsAll(project.android.sourceSets.demoRelease.java.srcDirs as List<String>)
        reportDemoRelease.dataFiles == ["${project.buildDir}/coverage/demoRelease/${CoverageContext.reportFileName}"] as List<String>

        and: "A reporting task is created for the full debug variant."
        ReportingTask reportFullDebug = project.tasks.findByName("reportFullDebug") as ReportingTask
        reportFullDebug != null
        reportFullDebug.destinationPath == "${project.buildDir.path}/coverage/fullDebug" as String
        reportFullDebug.description == "Generates coverage reports"
        reportFullDebug.reportingExtension == project.coverage.reporting
        reportFullDebug.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportFullDebug.sources.containsAll(project.android.sourceSets.debug.java.srcDirs as List<String>)
        reportFullDebug.sources.containsAll(project.android.sourceSets.full.java.srcDirs as List<String>)
        reportFullDebug.sources.containsAll(project.android.sourceSets.fullDebug.java.srcDirs as List<String>)
        reportFullDebug.dataFiles == ["${project.buildDir}/coverage/fullDebug/${CoverageContext.reportFileName}"] as List<String>

        and: "A reporting task is created for the full release variant."
        ReportingTask reportFullRelease = project.tasks.findByName("reportFullRelease") as ReportingTask
        reportFullRelease != null
        reportFullRelease.destinationPath == "${project.buildDir.path}/coverage/fullRelease" as String
        reportFullRelease.description == "Generates coverage reports"
        reportFullRelease.reportingExtension == project.coverage.reporting
        reportFullRelease.sources.containsAll(project.android.sourceSets.main.java.srcDirs as List<String>)
        reportFullRelease.sources.containsAll(project.android.sourceSets.release.java.srcDirs as List<String>)
        reportFullRelease.sources.containsAll(project.android.sourceSets.full.java.srcDirs as List<String>)
        reportFullRelease.sources.containsAll(project.android.sourceSets.fullRelease.java.srcDirs as List<String>)
        reportFullRelease.dataFiles == ["${project.buildDir}/coverage/fullRelease/${CoverageContext.reportFileName}"] as List<String>

        and: "A coverage check task is generated for every variant"
        project.android.applicationVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }

    def "Android Library project with flavours"() {
        given: "I have an Android library project"
        File manifest = new File (testProjectDir.newFolder("src", "main"), "AndroidManifest.xml")
        manifest << """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  package="com.psa.example">

         </manifest>
        """
        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.library'

        project.android {
            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                flavor1 {

                }

                flavor2 {

                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for every variant."
        project.android.libraryVariants.all {
            variant ->
                InstrumentationTask instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
                instrumentationTask != null
                instrumentationTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                instrumentationTask.instrumentationSettings == project.coverage.instrumentation
                instrumentationTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/${variant.dirName}")
        }

        and: "A reporting task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                ReportingTask reportTask = project.tasks.findByName("report${variant.name.capitalize()}") as ReportingTask
                reportTask != null
                reportTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                reportTask.description == "Generates coverage reports"
                reportTask.reportingExtension == project.coverage.reporting
                variant.sourceSets.each {
                    sourceSet ->
                        reportTask.sources.containsAll(sourceSet.java.srcDirs as List<String>)
                }
                reportTask.dataFiles == ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"] as List<String>
        }

        and: "A coverage checking task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }

    def "Android application with flavour dimensions"() {
        given: "I have an Android application with two flavours"
        Project project = ProjectBuilder.builder().build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.application'

        project.android {
            flavorDimensions "type", "city"

            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                applicationId "com.psa.example"
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                demo {
                    applicationId "com.psa.demo"
                    dimension "type"
                }

                full {
                    applicationId "com.psa.full"
                    dimension "type"
                }

                rome {
                    dimension "city"
                }

                athens {
                    dimension "city"
                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for every variant."
        project.android.applicationVariants.all {
            variant ->
                InstrumentationTask instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
                instrumentationTask != null
                instrumentationTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                instrumentationTask.instrumentationSettings == project.coverage.instrumentation
                instrumentationTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/${variant.dirName}")
        }

        and: "A reporting task is created for every variant"
        project.android.applicationVariants.all {
            variant ->
                ReportingTask reportTask = project.tasks.findByName("report${variant.name.capitalize()}") as ReportingTask
                reportTask != null
                reportTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                reportTask.description == "Generates coverage reports"
                reportTask.reportingExtension == project.coverage.reporting
                variant.sourceSets.each {
                    sourceSet ->
                        reportTask.sources.containsAll(sourceSet.java.srcDirs as List<String>)
                }
                reportTask.dataFiles == ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"] as List<String>
        }

        and: "A coverage checking task is created for every variant"
        project.android.applicationVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }

    def "Android library with flavour dimensions"() {
        given: "I have an Android library project"
        File manifest = new File (testProjectDir.newFolder("src", "main"), "AndroidManifest.xml")
        manifest << """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  package="com.psa.example">

         </manifest>
        """
        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.library'

        project.android {
            flavorDimensions "numerical", "cities"

            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                flavor1 {
                    dimension "numerical"
                }

                flavor2 {
                    dimension "numerical"
                }

                rome {
                    dimension "cities"
                }

                athens {
                    dimension "cities"
                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for every variant."
        project.android.libraryVariants.all {
            variant ->
                InstrumentationTask instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
                instrumentationTask != null
                instrumentationTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                instrumentationTask.instrumentationSettings == project.coverage.instrumentation
                instrumentationTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/${variant.dirName}")
        }

        and: "A reporting task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                ReportingTask reportTask = project.tasks.findByName("report${variant.name.capitalize()}") as ReportingTask
                reportTask != null
                reportTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                reportTask.description == "Generates coverage reports"
                reportTask.reportingExtension == project.coverage.reporting
                variant.sourceSets.each {
                    sourceSet ->
                        reportTask.sources.containsAll(sourceSet.java.srcDirs as List<String>)
                }
                reportTask.dataFiles == ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"] as List<String>
        }

        and: "A coverage checking task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }

    def "Android application with Kotlin plugin"() {
        given: "I have an Android application with two flavours"
        Project project = ProjectBuilder.builder().build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.application'
        project.apply plugin: 'kotlin-android'

        project.android {
            flavorDimensions "type", "city"

            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                applicationId "com.psa.example"
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                demo {
                    applicationId "com.psa.demo"
                    dimension "type"
                }

                full {
                    applicationId "com.psa.full"
                    dimension "type"
                }

                rome {
                    dimension "city"
                }

                athens {
                    dimension "city"
                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for every variant."
        project.android.applicationVariants.all {
            variant ->
                InstrumentationTask instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
                instrumentationTask != null
                instrumentationTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                instrumentationTask.instrumentationSettings == project.coverage.instrumentation
                instrumentationTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/${variant.dirName}")
        }

        and: "A reporting task is created for every variant"
        project.android.applicationVariants.all {
            variant ->
                ReportingTask reportTask = project.tasks.findByName("report${variant.name.capitalize()}") as ReportingTask
                reportTask != null
                reportTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                reportTask.description == "Generates coverage reports"
                reportTask.reportingExtension == project.coverage.reporting
                variant.sourceSets.each {
                    sourceSet ->
                        reportTask.sources.containsAll(sourceSet.java.srcDirs as List<String>)
                }
                reportTask.dataFiles == ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"] as List<String>
        }

        and: "A coverage checking task is created for every variant"
        project.android.applicationVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }

    def "Android library with Kotlin plug in"() {
        given: "I have an Android library project"
        File manifest = new File (testProjectDir.newFolder("src", "main"), "AndroidManifest.xml")
        manifest << """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                  package="com.psa.example">

         </manifest>
        """
        Project project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()
        project.buildscript {
            repositories {
                jcenter()
            }

            dependencies {
                classpath 'com.android.tools.build:gradle:2.2.0'
            }
        }
        project.apply plugin: 'com.android.library'
        project.apply plugin: 'kotlin-android'

        project.android {
            flavorDimensions "numerical", "cities"

            buildToolsVersion "24.0.2"

            compileSdkVersion 24

            defaultConfig {
                minSdkVersion 15
                targetSdkVersion 24
                versionCode 1
                versionName "1.0"
                testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
                vectorDrawables.useSupportLibrary = true
            }

            productFlavors {
                flavor1 {
                    dimension "numerical"
                }

                flavor2 {
                    dimension "numerical"
                }

                rome {
                    dimension "cities"
                }

                athens {
                    dimension "cities"
                }
            }
        }

        when: "I apply the coverage plugin"
        project.apply plugin: 'com.psa.coverage'
        project.evaluate()

        then: "A coverage extension is created"
        project.coverage instanceof CoverageExtension

        and: "The default gateway is configured"
        CoverageContext.coverageGateway instanceof CoberturaCoverageGateway
        CoverageContext.reportFileName == "cobertura.ser"

        and: "A coverage configuration is created"
        project.configurations["coverage"] != null
        !project.configurations['coverage'].dependencies.empty
        def coberturaDependency = project.configurations['coverage'].dependencies[0]
        coberturaDependency.group == "net.sourceforge.cobertura"
        coberturaDependency.name == "cobertura"
        coberturaDependency.version == "2.1.1"

        and: "An instrumentation task is created for every variant."
        project.android.libraryVariants.all {
            variant ->
                InstrumentationTask instrumentationTask = project.tasks.findByName("instrument${variant.name.capitalize()}") as InstrumentationTask
                instrumentationTask != null
                instrumentationTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                instrumentationTask.instrumentationSettings == project.coverage.instrumentation
                instrumentationTask.instrumentationClasses.contains("${project.buildDir}/intermediates/classes/${variant.dirName}")
        }

        and: "A reporting task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                ReportingTask reportTask = project.tasks.findByName("report${variant.name.capitalize()}") as ReportingTask
                reportTask != null
                reportTask.destinationPath == "${project.buildDir.path}/coverage/${variant.name}" as String
                reportTask.description == "Generates coverage reports"
                reportTask.reportingExtension == project.coverage.reporting
                variant.sourceSets.each {
                    sourceSet ->
                        reportTask.sources.containsAll(sourceSet.java.srcDirs as List<String>)
                }
                reportTask.dataFiles == ["${project.buildDir}/coverage/${variant.name}/${CoverageContext.reportFileName}"] as List<String>
        }

        and: "A coverage checking task is created for every variant"
        project.android.libraryVariants.all {
            variant ->
                CoverageCheckingTask coverageCheck = project.tasks.findByName("coverageCheck${variant.name.capitalize()}") as CoverageCheckingTask
                coverageCheck != null
                coverageCheck.dataFile == "${project.buildDir}/coverage/${variant.dirName}/${CoverageContext.reportFileName}" as String
                coverageCheck.description == "Performs coverage check"
                coverageCheck.checkingExtension == project.coverage.coverageChecking
        }
    }
}
