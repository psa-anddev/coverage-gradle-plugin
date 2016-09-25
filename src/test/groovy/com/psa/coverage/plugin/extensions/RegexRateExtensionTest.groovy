package com.psa.coverage.plugin.extensions

import spock.lang.*

/**
 * Feature: As a user, I want to be able to create regular expression rates in
 * a DSL fashion so that it is easier to read.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class RegexRateExtensionTest extends Specification {

    @SuppressWarnings("GrUnresolvedAccess")
    def "Create a regular expression rate in a DSL fashion"() {
        when: "I create a regular expression rate in DSL fashion"
        RegexRateExtension regexRateExtension = RegexRateExtension.build({
            regex "*.M"
            line 26
            branch 10
        })

        then:"the values are correct"
        regexRateExtension.regex == "*.M"
        regexRateExtension.rate.line == 26
        regexRateExtension.rate.branch == 10
    }
}
