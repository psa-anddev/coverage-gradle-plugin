package com.psa.coverage.plugin.extensions
/**
 * Allows the definition of a rate as an object.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class RateExtension {
    int line, branch;

    def static rate(closure) {
        RateExtension rateExtension = new RateExtension()
        closure.delegate = rateExtension
        closure()
        rateExtension
    }

    def line(int line) {
        this.line = line
    }

    def branch(int branch) {
        this.branch = branch
    }
}
