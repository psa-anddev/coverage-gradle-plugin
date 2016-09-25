package com.psa.coverage.checking

/**
 * This is the input boundary for the coverage checking use case.
 */
interface CoverageCheckingInput {
    void checkCoverage(CoverageCheckingRequest request, CoverageCheckingOutput output)
}