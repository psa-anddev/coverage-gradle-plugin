package com.psa.coverage.plugin.extensions

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*

/**
 * Feature: As a user, I want to configure the coverage plug in in a DSL fashion
 * so that it is more readable.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageExtensionTest extends Specification {

    @SuppressWarnings("GrUnresolvedAccess")
    def "Using a coverage extension in a DSL fashion"() {
        given: "I have a gradle project"
        Project project = ProjectBuilder.builder().build()

        when: "I apply a coverage extension"
        CoverageExtension extension = project.extensions.create("coverage", CoverageExtension)

        and: "I configure it"
        project.coverage {
            instrumentation {
                ignore "ignore1"
                ignore "ignore2"

                includeClass "*.M"
                includeClass "*.G"

                excludeClass "*.P"
                excludeClass "*.S"

                ignoreMethodAnnotation "BindViews"
                ignoreMethodAnnotation "Provides"

                ignoreTrivial true

                auxClasspath "/some/other/class/path"
            }

            reporting {
                format 'html'
                format 'xml'
                charset 'utf-8'
            }

            coverageChecking {
                classRate {
                    line 26
                    branch 1
                }

                packageRate {
                    line 12
                    branch 10
                }

                totalRate {
                    line 14
                }

                regexRate {
                    regex "*.MG"
                    line 26
                    branch 85
                }

                regexRate {
                    regex"*.ILMG"
                    line 14
                    branch 2
                }
            }
        }

        then: "The configuration is the expected one"
        extension.instrumentation instanceof InstrumentationExtension
        extension.reporting instanceof ReportingExtension
        extension.coverageChecking instanceof CheckingExtension
    }

    def "Creating an empty coverage extension"() {
        when: "I create an empty coverage extension"
        CoverageExtension extension = new CoverageExtension()

        then: "Instrumentation extension is not null"
        extension.instrumentation != null

        and: "reporting extension is not null"
        extension.reporting != null

        and: "coverage checking extension is not null"
        extension.coverageChecking != null
    }
}
