package com.psa.coverage.configuration

/**
 * This object holds the instrumentation configuration that is to be passed to the gateway.
 *
 * @author Pablo Sánchez Alonso
 */
class Instrumentation {
    /**
     * Specify the base directories containing the classes that are going to be instrumented.
     */
    List<String> basePaths = [];

    /**
     * Specify the name of the file to use for storing the metadata about the instrumented classes. This is a single file
     * containing serialized Java classes. It contains information about the names of the classes in your project, their
     * method names, line numbers, etc and it will be updated when the test are run.
     */
    String dataFile = ""

    /**
     * Defines an output directory for the instrumented classes. If none is defined, uninstrumented classes will be overwritten.
     */
    String destination = ""

    /**
     * Specify a filter to ignore certain lines in the code. This is useful to remove logging statements i.e.
     */
    List<String> ignores = []

    /**
     * Defines a series of regular expressions that tells which classes are to be included.
     */
    List<String> includeClasses = []
    /**
     * Defines a series of regular expressions that tells which classes are to be ignored.
     */
    List<String> excludeClasses = []

    /**
     * Tells the coverage tool to ignore methods which are annotated with the given annotations.
     */
    List<String> ignoreMethodAnnotations = []

    /**
     * Ignore trivial lines
     */
    boolean ignoreTrivial

    /**
     * Add any classes which the coverage tool would be unable to find.
     */
    String auxClassPath = ""

    /**
     * Defines the classes to be instrumented.
     */
    List<String> classes = []
}
