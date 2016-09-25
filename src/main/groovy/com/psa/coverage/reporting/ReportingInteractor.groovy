package com.psa.coverage.reporting

import com.psa.coverage.configuration.Reporting
import com.psa.coverage.helpers.CoverageContext

/**
 * This class is the reporting interactor.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class ReportingInteractor implements ReportingInput {
    void generateReports(ReportingRequest request, ReportingOutput output) {
        if (request.dataFiles.size() > 1)
            CoverageContext.coverageGateway.merge(getMergedDataFilePath(request), request.dataFiles)

        CoverageContext.coverageGateway.generateReport(generateReportingConfiguration(request))
        output.processResponse(new ReportingResponse(destinationPath: getReportsDestination(request)))
    }

    /**
     * Generates the reporting configuration.
     * @param request is the request object.
     * @return the report configuration object.
     */
    private static Reporting generateReportingConfiguration(ReportingRequest request) {
        def reporting = new Reporting()
        reporting.dataFile = getMergedDataFilePath(request)
        reporting.destination = getReportsDestination(request)
        reporting.formats = request.formats
        reporting.encoding = request.charset.toString()
        reporting.source = request.sources
        reporting
    }

    /**
     * Returns the merged data file path.
     * @param request is the request object
     * @return the path of the data file to generate reports.
     */
    private static String getMergedDataFilePath(ReportingRequest request) {
        if (request.dataFiles.size() == 1)
            request.dataFiles[0]
        else
            "${request.destinationPath}/merged.ser"
    }

    /**
     * Gets the destination path for the reports.
     * @param request is the request object.
     * @return is the destination path for the reports.
     */
    private static GString getReportsDestination(ReportingRequest request) {
        "${request.destinationPath}/reports"
    }
}
