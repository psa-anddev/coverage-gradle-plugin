package com.psa.coverage.plugin.extensions

import java.nio.charset.Charset

/**
 * This extension configures the reporting.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ReportingExtension {
    List<String> formats
    Charset charset

    def static build(closure) {
        ReportingExtension extension = new ReportingExtension(formats: [], charset: Charset.defaultCharset())
        closure.delegate = extension
        closure()
        extension
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def format(String format) {
        formats << format
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def charset(String charset) {
        this.charset = Charset.forName(charset)
    }
}
