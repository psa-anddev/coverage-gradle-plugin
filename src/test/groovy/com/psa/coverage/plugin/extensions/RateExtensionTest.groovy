package com.psa.coverage.plugin.extensions

import spock.lang.Specification

/**
 * Feature: As a user, I want to be able to define rates in a DSL fashion so that
 * it is easier to read.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class RateExtensionTest extends Specification {
    @SuppressWarnings("GrUnresolvedAccess")
    def "Define a rate in DSL fashion"() {
        when: "I define a rate"
        RateExtension rateExtension = RateExtension.rate({
            line 26
            branch 10
        })

        then: "The values are properly established"
        rateExtension.line == 26
        rateExtension.branch == 10
    }
}
