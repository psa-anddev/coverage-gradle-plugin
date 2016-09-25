package com.psa.coverage.reporting

import java.nio.charset.Charset

/**
 * This class is the reporting request
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ReportingRequest {
    /**
     * It is the destination path for the generated files.
     */
    String destinationPath
    /**
     * Metadata for the report.
     */
    List<String> dataFiles
    /**
     * It's the list of formats in which the report will be delivered.
     */
    List<String> formats;
    /**
     * It's the encoding in which to generate the report.
     */
    Charset charset;
    /**
     * It's the list of sources to generate the report with.
     */
    List<String> sources;
}
