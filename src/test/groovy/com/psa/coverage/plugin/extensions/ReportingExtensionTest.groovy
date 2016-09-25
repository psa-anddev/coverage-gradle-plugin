package com.psa.coverage.plugin.extensions

import spock.lang.*

import java.nio.charset.Charset

/**
 * Feature: As a user, I want to be able to define the reporting extension in a DSL fashion so
 * that it is more readable.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ReportingExtensionTest extends Specification {

    @SuppressWarnings("GrUnresolvedAccess")
    def "Building the reporting extension in a DSL fashion"() {

        when: "I build a reporting extension in a DSL fashion"
        ReportingExtension extension = ReportingExtension.build({
            format 'html'
            format 'xml'
            charset 'utf-8'
        })

        then: "the values are the expected ones"
        extension.formats[0] == 'html'
        extension.formats[1] == 'xml'
        extension.charset == Charset.forName('utf-8')
    }
}
