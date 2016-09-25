package com.psa.coverage.plugin.extensions

/**
 * Sets up a new Regular expression rate in the plug in.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class RegexRateExtension {
    String regex;
    RateExtension rate;

    def static build(closure) {
        RegexRateExtension regexRateExtension = new RegexRateExtension(rate: new RateExtension())
        closure.delegate = regexRateExtension
        closure()
        regexRateExtension
    }

    def regex(String regex) {
        this.regex = regex
    }

    def line(int line) {
        this.rate.line = line
    }

    def branch(int branch) {
        this.rate.branch = branch
    }
}
