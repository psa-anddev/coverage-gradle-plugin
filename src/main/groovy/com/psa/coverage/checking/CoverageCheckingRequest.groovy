package com.psa.coverage.checking

/**
 * This is the coverage checking request object.
 *
 * @author Pablo Sánchez Alonso
 * @version 1.0
 */
class CoverageCheckingRequest {
    /**
     * It is the path of the metadata file.
     */
    String dataFile;
    /**
     * It is the line rate per class.
     */
    int classLineRate;
    /**
     * It is the branch rate per class.
     */
    int classBranchRate;
    /**
     * It is the line rate per package
     */
    int packageLineRate;
    /**
     * It is the branch rate per package
     */
    int packageBranchRate;
    /**
     * It is the total line rate.
     */
    int totalLineRate
    /**
     * It is the total branch rate.
     */
    int totalBranchRate
    /**
     * It is the list of regular expression elements for fine grained checkings.
     */
    List<RegexElement> regexElements
}
