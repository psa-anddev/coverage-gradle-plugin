package com.psa.coverage.helpers

/**
 * This class specifies a rate given the line and the branch minimum coverage.
 *
 * @author Pablo Sánchez Alonso
 */
class Rate {
    private int line;
    int branch;

    /**
     * @return the minimum line coverage as a percentage.
     */
    int getLine() {
        return line
    }

    /**
     * Sets the minimum line coverage.
     * @param line minimum line coverage as a percentage
     * @throws IllegalArgumentException if the given value is less than 0 or greater than 100
     */
    void setLine(int line) {
        if (checkValue(line))
            throw new IllegalArgumentException("Line coverage rate has to be between 0 and 100.")
        this.line = line
    }

    void setBranch(int branch)
    {
        if (checkValue(branch))
            throw new IllegalArgumentException("Branch coverage has to be between 0 and 100")
        this.branch = branch
    }

    /**
     * Checks the value to be correct.
     * @param value is the value to check.
     * @return true when the value is less than 0 or greater than 100 which means that an exception has to be thrown.
     */
    private static boolean checkValue(int value) {
        value < 0 || value > 100
    }
}
