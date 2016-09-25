package com.psa.coverage.plugin.extensions
/**
 * It is the coverage plugin main extension.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageExtension {
    InstrumentationExtension instrumentation = instrumentation {}
    ReportingExtension reporting = reporting {}
    CheckingExtension coverageChecking = coverageChecking {}

    def instrumentation(closure) {
        instrumentation = InstrumentationExtension.build(closure)
    }

    def reporting(closure) {
        reporting = ReportingExtension.build(closure)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def coverageChecking(closure) {
        coverageChecking = CheckingExtension.build(closure)
    }
}
