package com.psa.coverage.configuration
/**
 * Defines a configuration object for reporting.
 *
 * @author Pablo Sánchez Alonso
 */
class Reporting {
    /**
     * Defines a base directory which is the root of any other given path.
     */
    String baseDir;

    /**
     * Specifies the file which contains the metadata for the instrumented classes.
     */
    String dataFile;

    /**
     * Specifies the output directory for the report.
     */
    String destination;
    /**
     * Specifies the formats in which the report is going to be generated.
     */
    List<String> formats;

    /**
     * Defines the encoding to use with the report.
     */
    String encoding;
    /**
     * Defines the source directories for the report.
     */
    List<String> source;
}
