package com.psa.coverage.plugin.extensions

import spock.lang.*

/**
 * Feature: As a user, I want to be able to build the coverage checking configuration
 * in a DSL fashion so that it is easier to read.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CheckingExtensionTest extends Specification {

    @SuppressWarnings("GrUnresolvedAccess")
    def "Building a checking extension in a DSL fashion"() {

        when: "I build a checking extension in a DSL fashion"
        CheckingExtension checkingExtension = CheckingExtension.build({
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
        })

        then:"All the values are the expected ones"
        checkingExtension.classRate.line == 26
        checkingExtension.classRate.branch == 1
        checkingExtension.packageRate.line == 12
        checkingExtension.packageRate.branch == 10
        checkingExtension.totalRate.line == 14
        checkingExtension.regexRates[0].regex == "*.MG"
        checkingExtension.regexRates[0].rate.line == 26
        checkingExtension.regexRates[0].rate.branch == 85
        checkingExtension.regexRates[1].regex == "*.ILMG"
        checkingExtension.regexRates[1].rate.line == 14
        checkingExtension.regexRates[1].rate.branch == 2
    }
}
