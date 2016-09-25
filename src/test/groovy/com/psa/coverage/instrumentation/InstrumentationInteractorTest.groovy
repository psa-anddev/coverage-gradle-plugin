package com.psa.coverage.instrumentation

import com.psa.coverage.configuration.Instrumentation
import com.psa.coverage.gateways.CoverageGateway
import com.psa.coverage.helpers.CoverageContext
import spock.lang.*

/**
 * Tests for the instrumentation interactor.
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class InstrumentationInteractorTest extends Specification {
    def "Instrumentation"() {
        def gateway = GroovyMock(CoverageGateway)
        CoverageContext.coverageGateway = gateway
        def output = GroovyMock(InstrumentationOutput)

        given: "I have an instrumentation request"
        InstrumentationRequest request = new InstrumentationRequest(
                destinationPath: "/some/destination/path", includeClasses: [".*com.psa.*"], excludeClasses: [".*R"],
                ignoreMethodsAnnotations: ["Provides", "BindViews"], ignores: ["System.out.println"], ignoreTrivial: true,
                auxClasspath: "/some/aux/classpath", instrumentationClasses: ["/some/instrumenteation/classes"]
        )

        when: "I call the instrumentation interactor"
        InstrumentationInteractor instrumentation = new InstrumentationInteractor()
        instrumentation.instrument(request, output)

        then: "Instrumentation gateway method is called"
        1 * gateway.instrument({
            it.basePaths == [] && it.dataFile == "${request.destinationPath}/cobertura.ser" &&
                    it.destination == "${request.destinationPath}/instrumentation" && it.ignores == request.ignores &&
                    it.includeClasses == request.includeClasses && it.excludeClasses == request.excludeClasses &&
                    it.ignoreMethodAnnotations == request.ignoreMethodsAnnotations && it.ignoreTrivial == request.ignoreTrivial &&
                    it.auxClassPath == request.auxClasspath && it.classes == request.instrumentationClasses
        } as Instrumentation)

        and: "View model is generated"
        1 * output.processResponse({
            it.instrumentationFilePath == "${request.destinationPath}/cobertura.ser"
        } as InstrumentationResponse)
    }
}
