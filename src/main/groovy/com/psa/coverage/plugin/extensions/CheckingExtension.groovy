package com.psa.coverage.plugin.extensions

/**
 * This extension allows the configuration of the coverage checking.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CheckingExtension {
    RateExtension classRate, packageRate, totalRate
    List<RegexRateExtension> regexRates

    def static build(closure) {
        CheckingExtension checkingExtension = new CheckingExtension(regexRates: [], classRate: new RateExtension(),
                packageRate: new RateExtension(), totalRate: new RateExtension())
        closure.delegate = checkingExtension
        closure()
        checkingExtension
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def classRate(closure){
        classRate = RateExtension.rate(closure)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def packageRate(closure) {
        packageRate = RateExtension.rate(closure)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def totalRate(closure) {
        totalRate = RateExtension.rate(closure)
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def regexRate(closure) {
        regexRates << RegexRateExtension.build(closure)
    }
}
