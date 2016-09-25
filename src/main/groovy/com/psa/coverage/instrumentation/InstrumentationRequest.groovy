package com.psa.coverage.instrumentation

/**
 * This is a request for instrumentation.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class InstrumentationRequest {
    /**
     * It is the destination folder for all the coverage files.
     */
    String destinationPath;
    /**
     * List of lines of code to ignore in the instrumentation process.
     */
    List<String> ignores = []
    /**
     * Classes to include in the instrumentation.
     */
    List<String> includeClasses = []
    /**
     * Classes to be excluded in the instrumentation (evaluated after the included classes).
     */
    List<String> excludeClasses = []
    /**
     * Method annotations to ignore.
     */
    List<String> ignoreMethodsAnnotations = []
    /**
     * When true, trivial lines are ignored.
     */
    boolean ignoreTrivial
    /**
     * Includes classes that are required for the coverage but that are not accessible directly by the coverage tool.
     */
    String auxClasspath = ""
    /**
     * Includes the paths of the classes to be instrumented.
     */
    List<String> instrumentationClasses = []
}
