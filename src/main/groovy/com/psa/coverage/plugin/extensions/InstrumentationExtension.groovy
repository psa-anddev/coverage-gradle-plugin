package com.psa.coverage.plugin.extensions

/**
 * Extension for instrumentation
 *
 * @author Pablo Sanchez Alonso
 * @version 1.0
 */
class InstrumentationExtension {
    /**
     * Indicate lines of code to be ignored during instrumentation.
     */
    List<String> ignores
    /**
     * List of classes to be taken into account in the coverage.
     */
    List<String> includeClasses
    /**
     * List of classes to exclude from the code coverage after taking include classes
     * into consideration.
     */
    List<String> excludeClasses
    /**
     * List of method annotations to ignore during the code coverage process.
     */
    List<String> ignoreMethodAnnotations
    /**
     * When true, trivial code is ignored.
     */
    boolean ignoreTrivial
    /**
     * Auxiliary classpath to use.
     */
    String auxClassPath

    def static build(closure) {
        InstrumentationExtension extension = new InstrumentationExtension(
                ignores: [], includeClasses: [], excludeClasses: [], ignoreMethodAnnotations: []
        )
        closure.delegate = extension
        closure()
        extension
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def ignore(String ignore) {
        this.ignores << ignore
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def includeClass(String classDef) {
        this.includeClasses << classDef
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def excludeClass(String classDef) {
        excludeClasses << classDef
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    def ignoreMethodAnnotation(String methodAnnotation) {
        ignoreMethodAnnotations << methodAnnotation
    }

    def ignoreTrivial(boolean ignoreTrivial) {
        this.ignoreTrivial = ignoreTrivial
    }

    def auxClasspath(String auxClasspath) {
        this.auxClassPath = auxClasspath
    }
}
