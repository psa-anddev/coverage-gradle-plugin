package com.psa.coverage.plugin.extensions

import spock.lang.*

/**
 * Feature: As a user, I want to be able to define the instrumentation data in a DSL fashion
 * so that it is easier to read.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class InstrumentationExtensionTest extends Specification {

    @SuppressWarnings("GrUnresolvedAccess")
    def "Creating instrumentation extension in DSL fashion"() {

        when: "I create an instrumentation extension in a DSL fashion"
        InstrumentationExtension extension = InstrumentationExtension.build({
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
        })

        then: "The values are the expected ones"
        extension.ignores[0] == "ignore1"
        extension.ignores[1] == "ignore2"
        extension.includeClasses[0] == "*.M"
        extension.includeClasses[1] == "*.G"
        extension.excludeClasses[0] == "*.P"
        extension.excludeClasses[1] == "*.S"
        extension.ignoreMethodAnnotations[0] == "BindViews"
        extension.ignoreMethodAnnotations[1] == "Provides"
        extension.ignoreTrivial
        extension.auxClassPath == "/some/other/class/path"
    }
}
