package com.psa.coverage.gateways

import com.psa.coverage.configuration.CoverageChecking
import com.psa.coverage.configuration.Instrumentation
import com.psa.coverage.configuration.Reporting
import com.psa.coverage.helpers.Rate
import com.psa.coverage.helpers.RegexRate
import net.sourceforge.cobertura.check.CheckCoverageMain
import net.sourceforge.cobertura.instrument.InstrumentMain
import net.sourceforge.cobertura.merge.MergeMain
import net.sourceforge.cobertura.reporting.ReportMain

/**
 * Gateway for Cobertura coverage
 *
 * @author Pablo Sánchez Alonso
 */
class CoberturaCoverageGateway implements CoverageGateway {
    void instrument(Instrumentation instrumentation) {
        List<String> args = new ArrayList<>()

        addBaseDirs(instrumentation.basePaths, args)
        addDataFile(instrumentation.dataFile, args)
        addDestination(instrumentation.destination, args)
        addIgnores(instrumentation.ignores, args)
        addIncludeClasses(instrumentation.includeClasses, args)
        addExcludeClasses(instrumentation.excludeClasses, args)
        addIgnoreMethodAnnotation(instrumentation.ignoreMethodAnnotations, args)
        addAuxClasspath(instrumentation.auxClassPath, args)
        addIgnoreTrivial(instrumentation.ignoreTrivial, args)
        addClasses(instrumentation.classes, args)

        InstrumentMain.instrument(convertArguments(args))
    }

    void generateReport(Reporting reporting) {
        List<String> args = new ArrayList<>()

        if (reporting.baseDir != null && !reporting.baseDir.isEmpty())
            addBaseDirs([reporting.baseDir], args)

        addDataFile(reporting.dataFile, args)
        addEncoding(reporting.encoding, args)
        addDestination(reporting.destination, args)

        if (reporting.formats == null || reporting.formats.isEmpty()) {
            runReporting(reporting, args)
        }
        else
            reporting.formats.forEach({
                List<String> argsWithFormat = new ArrayList<>()
                argsWithFormat.addAll(args)
                argsWithFormat.add("--format")
                argsWithFormat.add(it)
                runReporting(reporting, argsWithFormat)
            })
    }

    void check(CoverageChecking checking) {
        def args = new ArrayList<String>()
        if (checking != null) {
            addDataFile(checking.dataFile, args)
            addClassRate(checking.classRate, args)
            addPackageRate(checking.packageRate, args)
            addTotalRate(checking.totalRate, args)
            addRegularExpressionRates(checking.regexRateList, args)
        }
        CheckCoverageMain.checkCoverage(convertArguments(args))
    }

    void merge(String dataFile, List<String> reports) {
        def args = new ArrayList<String>()
        addDataFile(dataFile, args)
        addReports(reports, args)
        MergeMain.merge(convertArguments(args))
    }

    /**
     * Adds the list of regular expression rates.
     * @param regexRates is the list of regular expression rates
     * @param args is the list of arguments that have already been processed.
     */
    private static void addRegularExpressionRates(List<RegexRate> regexRates, List<String> args) {
        if (regexRates != null && !regexRates.isEmpty()) {
            regexRates.forEach({
                addRegexRate(it, args)
            })
        }
    }

    /**
     * Adds a regular expression rate.
     * @param regexRate is the regular expression rate to add
     * @param args is the list of arguments that have already been processed.
     */
    private static void addRegexRate(RegexRate regexRate, List<String> args) {
        args.add("--regex")
        args.add("${regexRate.regex}:${regexRate.rate.branch}:${regexRate.rate.line}")
    }

    /**
     * Add the total rate for the coverage.
     * @param totalRate is the total rate to add
     * @param args is the list of already processed arguments.
     */
    private static void addTotalRate(Rate totalRate, ArrayList<String> args) {
        if (totalRate != null) {
            args.add("--totalline")
            args.add("${totalRate.line}")
            args.add("--totalbranch")
            args.add("${totalRate.branch}")
        }
    }

    /**
     * Adds the package rate.
     * @param packageRate is the package rate to add.
     * @param args is the list of already processed arguments.
     */
    private static void addPackageRate(Rate packageRate, ArrayList<String> args) {
        if (packageRate != null) {
            args.add("--packageline")
            args.add("${packageRate.line}")
            args.add("--packagebranch")
            args.add("${packageRate.branch}")
        }
    }

    /**
     * Adds the class rate.
     * @param classRate is a rate object to denote the rate.
     * @param args is the list of already processed arguments.
     */
    private static void addClassRate(Rate classRate, ArrayList<String> args) {
        if (classRate != null) {
            args.add("--line")
            args.add("${classRate.line}")
            args.add("--branch")
            args.add("${classRate.branch}")
        }
    }

    /**
     * Adds the reports.
     * @param reports is the list of reports
     * @param args is the list of already processed arguments.
     */
    private static void addReports(List<String> reports, args) {
        if (reports != null && !reports.isEmpty())
            reports.forEach({
                args.add(it)
            })
    }

    /**
     * Runs the report generation tool.
     *
     * @param reporting is the reporting object.
     * @param args is the list of arguments already processed.
     */
    private static void runReporting(Reporting reporting, ArrayList<String> args) {
        addSourceDirectories(reporting.source, args)
        ReportMain.generateReport(convertArguments(args))
    }

    /**
     * Converts the list of arguments into an array.
     * @param args list of arguments
     * @return array of arguments.
     */
    private static String[] convertArguments(ArrayList<String> args) {
        String[] argsArray = new String[args.size()]
        for (int index = 0; index < args.size(); index++) {
            argsArray[index] = args.get(index)
        }
        argsArray
    }

    /**
     * Adds the encoding parameter.
     * @param encoding is the encoding to add.
     * @param args is the list of already processed arguments.
     */
    private static void addEncoding(String encoding, ArrayList<String> args) {
        if (encoding != null && !encoding.isEmpty()) {
            args.add("--encoding")
            args.add(encoding)
        }
    }

    /**
     * Adds the source directories.
     * @param source is the list of source folders
     * @param args is the list of arguments
     */
    private static void addSourceDirectories(List<String> source, ArrayList<String> args) {
        if (source != null && !source.isEmpty())
            source.forEach({
                args.add(it)
            })
    }


    /**
     * Adds the data file parameter
     * @param dataFile is the value of the data file
     * @param args is the list of arguments.
     */
    private static void addDataFile(String dataFile, ArrayList<String> args) {
        if (dataFile != null && dataFile.length() > 0) {
            args.add("--datafile")
            args.add(dataFile)
        }
    }

    /**
     * Add the base directories to the list of arguments.
     * @param baseDirs is the list of base directories.
     * @param args is the list of arguments
     * @return a list of booleans.
     */
    private static List<Boolean> addBaseDirs(List<String> baseDirs, args) {
        baseDirs.collect(
                {
                    args.add("--basedir")
                    args.add(it)
                }
        )
    }

    /**
     * Adds the destination parameter
     * @param destination is the destination
     * @param args is the list of arguments
     */
    private static void addDestination(String destination, ArrayList<String> args) {
        if (destination != null && destination.length() > 0) {
            args.add("--destination")
            args.add(destination)
        }
    }

    /**
     * Add ignores.
     * @param ignores the list of ignored regular expressions
     * @param args is the list of arguments
     */
    private static void addIgnores(List<String> ignores, List<String> args) {
        ignores.forEach({
            args.add("--ignore")
            args.add(it)
        })

    }

    /**
     * Add include classes.
     * @param includeClasses list of include classes.
     * @param args Call arguments.
     */
    private static void addIncludeClasses(List<String> includeClasses, List<String> args) {
        includeClasses.forEach({
            args.add("--includeClasses")
            args.add(it)
        })

    }

    /**
     * Add exclude classes.
     * @param excludeClasses list of exclude classes.
     * @param args Call arguments.
     */
    private static void addExcludeClasses(List<String> excludeClasses, List<String> args) {
        excludeClasses.forEach({
            args.add("--excludeClasses")
            args.add(it)
        })
    }

    /**
     * Add ignore method annotations
     * @param ignoreMethodAnnotations is the list of ignored method annotations
     * @param args is the list of arguments.
     */
    private static void addIgnoreMethodAnnotation(List<String> ignoreMethodAnnotations, List<String> args) {
        ignoreMethodAnnotations.forEach({
            args.add("--ignoreMethodAnnotation")
            args.add(it)
        })
    }

    /**
     * Add classes.
     * @param classes list of classes
     * @param args list of arguments
     */
    private static void addClasses(List<String> classes, List<String> args) {
       classes.forEach({
           args.add(it)
       })
    }

    /**
     * Add the auxiliary classpath
     * @param auxClasspath is the auxiliary classpath
     * @param args is the list of arguments
     */
    private static void addAuxClasspath(String auxClasspath, List<String> args) {
        if (auxClasspath.length() > 0)
        {
            args.add("--auxClasspath")
            args.add(auxClasspath)
        }
    }

    private static void addIgnoreTrivial(boolean ignoreTrivial, List<String> args) {
        if (ignoreTrivial)
            args.add("--ignoreTrivial")
    }
}
