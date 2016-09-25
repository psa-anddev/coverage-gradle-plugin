package com.psa.coverage.helpers

import spock.lang.*

/**
 * Rates tests.
 *
 * @author Pablo Sánchez Alonso
 */
class RateTest extends Specification {
    def "Line rate below 0 throws an illegal argument exception"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set the line coverage below 0"
        rate.line = -26

        then: "An illegal argument exception is thrown"
        IllegalArgumentException ex = thrown()
        ex.message == "Line coverage rate has to be between 0 and 100."
    }

    def "Line rate over 100 throws illegal argument exception"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set the line coverage above 100"
        rate.line = 300

        then: "An illegal argument exception is thrown"
        IllegalArgumentException ex = thrown()
        ex.message == "Line coverage rate has to be between 0 and 100."
    }

    def "Line rate is set if the value is correct"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set a correct value for the line rate"
        rate.line = 15

        then: "The value is set"
        rate.line == 15
    }

    def "Branch rate less than 0 throws illegal argument exception"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set the branch coverage to a value which is less than 0"
        rate.branch = -26

        then: "An illegal argument exception is thrown"
        IllegalArgumentException ex = thrown()
        ex.message == "Branch coverage has to be between 0 and 100"
    }

    def "Branch rate greater than 100 throws illegal argument exception"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set the branch coverage to a value which is greater than 100"
        rate.branch = 300

        then: "An Illegal argument exception is thrown"
        IllegalArgumentException ex = thrown()
        ex.message == "Branch coverage has to be between 0 and 100"
    }

    def "Branch rate between 0 and 100 sets the property"() {
        given: "I have a rate"
        Rate rate = new Rate()

        when: "I set a value between 0 and 100 for the branch coverage"
        rate.branch = 26

        then: "The value gets set."
        rate.branch == 26
    }
}
