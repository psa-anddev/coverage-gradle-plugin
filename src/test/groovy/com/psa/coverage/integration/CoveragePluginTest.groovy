package com.psa.coverage.integration

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

/**
 * Integration tests.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoveragePluginTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder();
    File buildFile;
    List<File> pluginClasspath

    def setup() {
        buildFile = testProjectDir.newFile("build.gradle")

        def pluginClasspathResource = getClass().classLoader.findResource("plugin-classpath.txt")

        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    }

    def "Java plugin without coverage tasks"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id 'java'
                id 'com.psa.coverage'
            }


        """

        when: "I execute a build without coverage tasks"
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("clean", "build")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "The instrumentation task is not executed"
        result.task(":instrument") == null

        and: "the reporting task is not executed"
        result.task(':report') == null

        and: "the coverage check task is not executed"
        result.task(':coverageCheck') == null
    }

    def "Execution with Java plugin of the instrumentation tasks"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id 'java'
                id 'com.psa.coverage'
            }

        """

        when: "I execute the instrumentation task"
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "Instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS
    }

    def "Execution of instrumentation and test with Java plugin"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id 'java'
                id 'com.psa.coverage'
            }
        """

        when: "I execute instrumentation and test"
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument", "test")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "Instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS

        and: "Test doesn't fail"
        result.task(":test").outcome == UP_TO_DATE
    }

    def "Execution of instrument, test and report with Java plugin"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id 'java'
                id 'com.psa.coverage'
            }
        """

        when: "I execute report"
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument", "test", "report")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "Instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS

        and: "Reporting is successful"
        result.task(":report").outcome == SUCCESS
    }

    def "Execution of instrumentation, test, reporting and coverage check with Java plugin"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id 'java'
                id 'com.psa.coverage'
            }

            coverage {
                coverageChecking {
                    classRate {
                        line 26
                        branch 10
                    }

                    packageRate {
                        line 12
                        branch 11
                    }

                    totalRate {
                        line 26
                        branch 1
                    }
                }
            }
        """

        when: "I execute report"
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument", "test", "report", "coverageCheck")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "Instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS

        and: "Reporting is successful"
        result.task(":report").outcome == SUCCESS

        and: "coverage check is successful."
        result.task(":coverageCheck").outcome == SUCCESS
    }

    def "No coverage tasks with Kotlin plugin"() {
        given: "I have a project with the Kotlin plugin"
        //TODO- Used com.zoltu.kotlin because the official plug in is not avaialble through the plug in annotation. Change when it is.
        buildFile << """
            plugins {
                id "com.zoltu.kotlin" version "1.0.3"
                id "com.psa.coverage"
            }


        """

        when: "I execute a build without coverage tasks"
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("clean", "build")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "The instrumentation task is not executed"
        result.task(":instrument") == null

        and: "the reporting task is not executed"
        result.task(':report') == null

        and: "The coverage check task is not executed"
        result.task(":coverageCheck") == null
    }

    def "Instrumentation task with Kotlin plug in"() {
        given: "I have a project with the Kotlin plugin"
        //TODO- Used com.zoltu.kotlin because the official plug in is not avaialble through the plug in annotation. Change when it is.
        buildFile << """
            plugins {
                id "com.zoltu.kotlin" version "1.0.3"
                id "com.psa.coverage"
            }


        """

        when: "I execute the instrumentation task"
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "The instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS
    }

    def "Execution of instrumentation, test and report with Kotlin plugin"() {
        given: "I have a project with the Kotlin plugin"
        //TODO- Used com.zoltu.kotlin because the official plug in is not avaialble through the plug in annotation. Change when it is.
        buildFile << """
            plugins {
                id "com.zoltu.kotlin" version "1.0.3"
                id "com.psa.coverage"
            }


        """

        when: "I execute the instrumentation, test and report tasks"
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument", "test", "report")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "The instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS

        and: "The reporting task is successful"
        result.task(":report").outcome == SUCCESS

        and: "The coverage check tas is not executed"
        result.task(":coverageCheck") == null
    }

    def "Execution of coverage check task with Kotlin plugin"() {
        given: "I have a project that applies the Java and the coverage plugin"
        buildFile << """
            plugins {
                id "com.zoltu.kotlin" version "1.0.3"
                id 'com.psa.coverage'
            }

            coverage {
                coverageChecking {
                    classRate {
                        line 26
                        branch 10
                    }

                    packageRate {
                        line 12
                        branch 11
                    }

                    totalRate {
                        line 26
                        branch 1
                    }
                }
            }
        """

        when: "I execute report"
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments("instrument", "test", "report", "coverageCheck")
                .withPluginClasspath(pluginClasspath)
                .build()

        then: "Instrumentation is successful"
        result.task(":instrument").outcome == SUCCESS

        and: "Reporting is successful"
        result.task(":report").outcome == SUCCESS

        and: "coverage check is successful."
        result.task(":coverageCheck").outcome == SUCCESS
    }
}
