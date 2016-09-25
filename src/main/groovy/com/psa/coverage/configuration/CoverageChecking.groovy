package com.psa.coverage.configuration

import com.psa.coverage.helpers.Rate
import com.psa.coverage.helpers.RegexRate

/**
 * Configuration for coverage checking
 */
class CoverageChecking {

    /**
     * Specify the name of the file containing the metadata for the classes.
     */
    String dataFile;
    /**
     * Defines the minimum rate per class.
     */
    Rate classRate;
    /**
     * Defines the minimum average coverage per package.
     */
    Rate packageRate;

    /**
     * Defines the minimum average coverage in the project as a whole.
     */
    Rate totalRate;

    /**
     * Allows to specify rates for individual classes given the regular expressions.
     */
    List<RegexRate> regexRateList;
}
