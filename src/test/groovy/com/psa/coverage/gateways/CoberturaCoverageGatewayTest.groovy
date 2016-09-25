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
import spock.lang.Specification

/**
 * Tests for the Cobertura gateway.
 *
 * @author Pablo Sanchez Alonso
 */
@SuppressWarnings("GroovyAssignabilityCheck")
class CoberturaCoverageGatewayTest extends Specification {

    def "Instrumentation without parameters"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I don't have any parameters"
        def instrumentation = new Instrumentation()

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "Cobertura doesn't add any base directory"
        1 * InstrumentMain.instrument([])
    }

    def "Instrumentation with one base directory"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one base directory"
        def instrumentation = new Instrumentation()
        instrumentation.basePaths << "/some/base/path"

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "the base directory is added as an argument"
        1 * InstrumentMain.instrument({
            it[0] == "--basedir" && it[1] == "/some/base/path"
        })
    }

    def "Instrumentation with two base directories"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two base directories"
        def instrumentation = new Instrumentation()
        instrumentation.basePaths << "/some/base/path"
        instrumentation.basePaths << "/some/other/base/path"

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "Both base directories are added to the instrumentation"
        1 * InstrumentMain.instrument({
            it[0] == "--basedir" && it[1] == "/some/base/path" &&
                    it[2] == "--basedir" && it[3] == "/some/other/base/path"
        })
    }

    def "Instrumentation with four base directories"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four base directories"
        def instrumentation = new Instrumentation()
        instrumentation.basePaths << "/some/base/path"
        instrumentation.basePaths << "/some/other/base/path"
        instrumentation.basePaths << "/some/third/base/path"
        instrumentation.basePaths << "/some/fourth/base/path"

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "All base directories are added to the instrumentation"
        1 * InstrumentMain.instrument({
            it[0] == "--basedir" && it[1] == "/some/base/path" &&
                    it[2] == "--basedir" && it[3] == "/some/other/base/path" &&
                    it[4] == "--basedir" && it[5] == "/some/third/base/path" &&
                    it[6] == "--basedir" && it[7] == "/some/fourth/base/path"
        })
    }

    def "Instrumentation with dataFile"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have a data file"
        Instrumentation instrumentation = new Instrumentation(dataFile: "/some/data/file")

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "the data file is added to the arguments"
        1 * InstrumentMain.instrument({
            it[0] == "--datafile" && it[1] == "/some/data/file"
        })
    }

    def "Instrument with destination"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have a destination"
        Instrumentation instrumentation = new Instrumentation(destination: "/some/destination")

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "the destination is added to the arguments"
        1 * InstrumentMain.instrument({
            it[0] == "--destination" && it[1] == "/some/destination"
        })
    }

    def "Instrument with one ignore"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one ignore"
        Instrumentation instrumentation = new Instrumentation(ignores: ["ignore"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The ignore is added to the arguments"
        1 * InstrumentMain.instrument({
            it[0] == "--ignore" && it[1] == "ignore"
        })
    }

    def "Instrument with two ignores"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two ignore"
        Instrumentation instrumentation = new Instrumentation(ignores: ["ignore", "ignore2"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The ignores are added to the arguments"
        1 * InstrumentMain.instrument({
            it[0] == "--ignore" && it[1] == "ignore"
            it[2] == "--ignore" && it[3] == "ignore2"
        })
    }

    def "Instrument with four ignores"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four ignore"
        Instrumentation instrumentation = new Instrumentation(ignores: ["ignore", "ignore2", "ignore3", "ignore4"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The ignores are added to the arguments"
        1 * InstrumentMain.instrument({
            it[0] == "--ignore" && it[1] == "ignore"
            it[2] == "--ignore" && it[3] == "ignore2"
            it[4] == "--ignore" && it[5] == "ignore3"
            it[6] == "--ignore" && it[7] == "ignore4"
        })
    }

    def "Instrumentation with one include classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one include class"
        Instrumentation instrumentation = new Instrumentation(includeClasses: [".*"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expression is added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--includeClasses" && it[1] == ".*"
        })
    }

    def "Instrumentation with two include classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two include class"
        Instrumentation instrumentation = new Instrumentation(includeClasses: [".*", ".*com"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expressions are added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--includeClasses" && it[1] == ".*" &&
            it[2] == "--includeClasses" && it[3] == ".*com"
        })
    }

    def "Instrumentation with four include classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four include class"
        Instrumentation instrumentation = new Instrumentation(includeClasses: [".*", ".*com", ".*com.psa", ".*com.psa.coverage"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expressions are added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--includeClasses" && it[1] == ".*" &&
                    it[2] == "--includeClasses" && it[3] == ".*com" &&
                    it[4] == "--includeClasses" && it[5] == ".*com.psa" &&
                    it[6] == "--includeClasses" && it[7] == ".*com.psa.coverage"
        })
    }

    def "Instrumentation with one exclude classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one exclude class"
        Instrumentation instrumentation = new Instrumentation(excludeClasses: [".*"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expression is added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--excludeClasses" && it[1] == ".*"
        })
    }

    def "Instrumentation with two exclude classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two exclude class"
        Instrumentation instrumentation = new Instrumentation(excludeClasses: [".*", ".*com"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expressions are added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--excludeClasses" && it[1] == ".*" &&
                    it[2] == "--excludeClasses" && it[3] == ".*com"
        })
    }

    def "Instrumentation with four exclude classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four exclude class"
        Instrumentation instrumentation = new Instrumentation(excludeClasses: [".*", ".*com", ".*com.psa", ".*com.psa.coverage"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The regular expressions are added to the parameters"
        1 * InstrumentMain.instrument({
            it[0] == "--excludeClasses" && it[1] == ".*" &&
                    it[2] == "--excludeClasses" && it[3] == ".*com" &&
                    it[4] == "--excludeClasses" && it[5] == ".*com.psa" &&
                    it[6] == "--excludeClasses" && it[7] == ".*com.psa.coverage"
        })
    }

    def "Instrumentation with one ignore method annotation"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one ignore method annotation"
        Instrumentation instrumentation = new Instrumentation(ignoreMethodAnnotations: ["Component"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The argument is added"
        1 * InstrumentMain.instrument({
            it[0] == "--ignoreMethodAnnotation" && it[1] == "Component"
        })
    }

    def "Instrumentation with two ignore method annotations"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two ignore method annotations"
        Instrumentation instrumentation = new Instrumentation(ignoreMethodAnnotations: ["Component", "Provides"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The argument is added"
        1 * InstrumentMain.instrument({
            it[0] == "--ignoreMethodAnnotation" && it[1] == "Component"
            it[2] == "--ignoreMethodAnnotation" && it[3] == "Provides"
        })
    }

    def "Instrumentation with four ignore method annotations"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four ignore method annotations"
        Instrumentation instrumentation = new Instrumentation(ignoreMethodAnnotations: ["Component", "Provides", "Module", "BindView"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The argument is added"
        1 * InstrumentMain.instrument({
            it[0] == "--ignoreMethodAnnotation" && it[1] == "Component"
            it[2] == "--ignoreMethodAnnotation" && it[3] == "Provides" &&
            it[4] == "--ignoreMethodAnnotation" && it[5] == "Module" &&
            it[6] == "--ignoreMethodAnnotation" && it[7] == "BindView"
        })
    }

    def "Instrumentation with one class"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have one class"
        Instrumentation instrumentation = new Instrumentation(classes: ["Class1"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "The class is added"
        1 * InstrumentMain.instrument({
            it[0] == "Class1"
        })
    }

    def "Instrumentation with two classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have two classes"
        Instrumentation instrumentation = new Instrumentation(classes: ["Class1", "Class2"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "Both classes are added"
        1 * InstrumentMain.instrument({
            it[0] == "Class1" && it[1] == "Class2"
        })
    }

    def "Instrumentation with four classes"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have four classes"
        Instrumentation instrumentation = new Instrumentation(classes: ["Class1", "Class2", "Class3", "Class4"])

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "All classes are added"
        1 * InstrumentMain.instrument({
            it[0] == "Class1" && it[1] == "Class2" && it[2] == "Class3" && it[3] == "Class4"
        })
    }

    def "Instrumentation with aux classpath"() {
        GroovyMock(InstrumentMain, global: true)

        given: "I have an auxiliary classpath"
        Instrumentation instrumentation = new Instrumentation(auxClassPath: "com.psa.aux")

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "the auxiliary classpath is added"
        1 * InstrumentMain.instrument({
            it[0] == "--auxClasspath" && it[1] == "com.psa.aux"
        })
    }

    def "Instrumentations with ignore trivial"() {
        GroovyMock(InstrumentMain, global: true)
        given: "I have set my instrumentation to ignore trivial"
        Instrumentation instrumentation = new Instrumentation(ignoreTrivial: true)

        when: "I instrument my classes"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.instrument(instrumentation)

        then: "the parameter is added"
        1 * InstrumentMain.instrument({
            it[0] == "--ignoreTrivial"
        })
    }

    def "Reporting without parameters"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object comes empty"
        Reporting reporting = new Reporting()

        when: "I generate the reports"
        CoberturaCoverageGateway coberturaCoverageGateway = new CoberturaCoverageGateway()
        coberturaCoverageGateway.generateReport(reporting)

        then: "Cobertura calls the reporting task without parameters"
        1 * ReportMain.generateReport({it.length == 0})
    }

    def "Reporting with one source"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object comes with one source folder"
        Reporting reporting = new Reporting(source: ["/some/source/dir"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura calls the reporting task with the source directory"
        1 * ReportMain.generateReport({
            it[0] == "/some/source/dir"
        })
    }

    def "Reporting with two sources"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains two sources"
        Reporting reporting = new Reporting(source: ["/some/source/dir", "/some/other/source/dir"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Both sources are added as parameters"
        1 * ReportMain.generateReport({
            it[0] == "/some/source/dir" && it[1] == "/some/other/source/dir"
        })
    }

    def "Reporting with four sources"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains four source folders"
        Reporting reporting = new Reporting(source: ["/some/source/dir", "/some/other/source/dir",
                                                     "/some/third/source/dir", "/some/fourth/source/dir"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura runs containing the four sources"
        1 * ReportMain.generateReport({
            it[0] == "/some/source/dir" && it[1] == "/some/other/source/dir" &&
            it[2] == "/some/third/source/dir" && it[3] == "/some/fourth/source/dir"
        })
    }

    def "Reporting with one format"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains one format"
        Reporting reporting = new Reporting(formats: ["html"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura runs with the given format"
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "html"
        })
    }

    def "Reporting with two formats"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains two formats"
        Reporting reporting = new Reporting(formats: ["html", "xml"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is called twice, once per format"
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "html"
        })
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "xml"
        })
    }

    def "Reporting with four formats"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains four formats"
        Reporting reporting = new Reporting(formats: ["html", "xml", "json", "pdf"])

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is called four times, one per format"
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "html"
        })
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "xml"
        })
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "json"
        })
        1 * ReportMain.generateReport({
            it[0] == "--format" && it[1] == "pdf"
        })
    }

    def "Reporting with base dir"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object comes with a base directory"
        Reporting reporting = new Reporting(baseDir: "/some/base/dir")

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is called with the base directory"
        1 * ReportMain.generateReport({
            it[0] == "--basedir" && it[1] == "/some/base/dir"
        })
    }

    def "Reporting with encoding"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object specifies the encoding"
        Reporting reporting = new Reporting(encoding: "UTF-8")

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is called specifying the encoding"
        1 * ReportMain.generateReport({
            it[0] == "--encoding" && it[1] == "UTF-8"
        })
    }

    def "Reporting with data file"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains the data file"
        Reporting reporting = new Reporting(dataFile: "/some/data/file")

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is run with the data file parameter"
        1 * ReportMain.generateReport({
            it[0] == "--datafile" && it[1] == "/some/data/file"
        })
    }

    def "Reporting with destination"() {
        GroovyMock(ReportMain, global: true)

        given: "The reporting object contains the destination"
        Reporting reporting = new Reporting(destination: "/some/destination")

        when: "I generate the report"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.generateReport(reporting)

        then: "Cobertura is called with the given destination"
        1 * ReportMain.generateReport({
            it[0] == "--destination" && it[1] == "/some/destination"
        })
    }

    def "Merge reports without parameters (null)"() {
        GroovyMock(MergeMain, global: true)

        given: "The data file is null"
        String dataFile = null

        and: "The list of reports is null"
        List<String> reports = null

        when: "I merge reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway();
        gateway.merge(dataFile, reports)

        then: "Cobertura is called without parameters"
        1 * MergeMain.merge({
            it.length == 0
        })
    }

    def "Merge reports without parameters (empty)"() {
        GroovyMock(MergeMain, global: true)

        given: "The data file is empty"
        String dataFile = ""

        and: "The list of reports is empty"
        List<String> reports = new ArrayList<>()

        when: "I merge the reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.merge(dataFile, reports)

        then: "Cobertura is called without parameters"
        1 * MergeMain.merge({
            it.length == 0
        })
    }

    def "Merge reports with data file but without reports"() {
        GroovyMock(MergeMain, global: true)
        given: "I have a data file"
        String dataFile = "/some/data/file"

        and: "I don't have any reports"
        List<String> reports = new ArrayList<>()

        when: "I merge the reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.merge(dataFile, reports)

        then: "Cobertura is called with the data file"
        1 * MergeMain.merge({
            it[0] == "--datafile" && it[1] == dataFile
        })
    }

    def "Merge reports with one report"() {
        GroovyMock(MergeMain, global: true)

        given: "I have a report"
        List<String> reports = new ArrayList<>()
        reports.add("/some/report")

        and: "I don't have a data file"
        String dataFile = ""

        when: "I merge reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.merge(dataFile, reports)

        then: "Cobertura is called with the given report"
        1 * MergeMain.merge({
            it[0] == reports[0]
        })
    }

    def "Merge reports with two reports"() {
        GroovyMock(MergeMain, global: true)

        given: "I have two reports"
        List<String> reports = new ArrayList<>()
        reports.add("/some/report")
        reports.add("/some/other/report")

        and: "I don't have a data file"
        String dataFile = ""

        when: "I merge reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.merge(dataFile, reports)

        then: "Cobertura is called with the two reports"
        1 * MergeMain.merge({
            it[0] == reports[0] && it[1] == reports[1]
        })
    }

    def "Merge reports with four reports"() {
        GroovyMock(MergeMain, global: true)

        given: "I have four reports"
        List<String> reports = ["/some/report", "/some/other/report", "/some/third/report", "/some/fourth/report"]

        and: "I don't have a data file"
        String dataFile = ""

        when: "I merge the reports"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.merge(dataFile, reports)

        then: "Cobertura is run with the four reports"
        1 * MergeMain.merge({
            it[0] == reports[0] && it[1] == reports[1] &&
            it[2] == reports[2] && it[3] == reports[3]
        })
    }

    def "Check coverage without parameters (null)"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The coverage configuration is null"
        CoverageChecking coverageChecking = null;

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(coverageChecking)

        then: "Cobertura is run without parameters"
        1 * CheckCoverageMain.checkCoverage({
            it.length == 0
        })
    }

    def "Check coverage without parameters (empty)"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The coverage configuration is empty"
        CoverageChecking coverageChecking = new CoverageChecking()

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(coverageChecking)

        then: "Cobertura runs without parameters"
        1 * CheckCoverageMain.checkCoverage({
            it.length == 0
        })
    }

    def "Check coverage with data file"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The data file is informed"
        CoverageChecking coverageChecking = new CoverageChecking(dataFile: "/some/data/file")

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(coverageChecking)

        then: "Cobertura runs with the data file parameter"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--datafile" && it[1] == coverageChecking.dataFile
        })
    }

    def "Check coverage with class coverage"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The class coverage is provided"
        CoverageChecking coverageChecking = new CoverageChecking(classRate: new Rate(line: 20, branch: 30))

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(coverageChecking)

        then: "Cobertura is called with the given class coverage"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--line" && it[1] == "${coverageChecking.classRate.line}" &&
            it[2] == "--branch" && it[3] == "${coverageChecking.classRate.branch}"
        })
    }

    def "Check coverage with package rate"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The package coverage is provided"
        CoverageChecking checking = new CoverageChecking(packageRate: new Rate(line: 20, branch: 60))

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(checking)

        then: "Cobertura is called with the given package coverage"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--packageline" && it[1] == "${checking.packageRate.line}" &&
            it[2] == "--packagebranch" && it[3] == "${checking.packageRate.branch}"
        })
    }

    def "Check coverage with total rate"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "The total coverage is provided"
        CoverageChecking checking = new CoverageChecking(totalRate: new Rate(line: 30, branch: 70))

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(checking)

        then: "Cobertura is called with the given total coverage"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--totalline" && it[1] == "${checking.totalRate.line}" &&
            it[2] == "--totalbranch" && it[3] == "${checking.totalRate.branch}"
        })
    }

    def "Check coverage with one regular expression rate"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "There is a regular expression rate in the coverage parameters"
        CoverageChecking checking = new CoverageChecking(
                regexRateList: [
                        new RegexRate(regex: ".*", rate: new Rate(line: 20, branch: 30))
                ])

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(checking)

        then: "Cobertura runs with the given regular expression rate"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--regex" &&
                    it[1] == "${checking.regexRateList[0].regex}:${checking.regexRateList[0].rate.branch}:${checking.regexRateList[0].rate.line}"
        })
    }

    def "Check coverage with two regular expression rates"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "There are two regular expression rates in the coverage parameters"
        CoverageChecking checking = new CoverageChecking(
                regexRateList: [
                        new RegexRate(regex: ".*", rate: new Rate(line: 20, branch: 30)),
                        new RegexRate(regex: ".*2", rate: new Rate(line: 26, branch: 1))
                ])

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(checking)

        then: "Cobertura runs with the two given regular expression rates"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--regex" &&
                    it[1] == "${checking.regexRateList[0].regex}:${checking.regexRateList[0].rate.branch}:${checking.regexRateList[0].rate.line}" &&
                    it[2] == "--regex" && it[3] == "${checking.regexRateList[1].regex}:${checking.regexRateList[1].rate.branch}:${checking.regexRateList[1].rate.line}"
        })
    }

    def "Check coverage with four regular expression rates"() {
        GroovyMock(CheckCoverageMain, global: true)

        given: "There are four regular expression rates in the coverage parameters"
        CoverageChecking checking = new CoverageChecking(
                regexRateList: [
                        new RegexRate(regex: ".*", rate: new Rate(line: 20, branch: 30)),
                        new RegexRate(regex: ".*2", rate: new Rate(line: 26, branch: 1)),
                        new RegexRate(regex: ".*3", rate: new Rate(line: 1, branch: 26)),
                        new RegexRate(regex: ".*4", rate: new Rate(line: 26, branch: 10))
                ])

        when: "I check my coverage"
        CoberturaCoverageGateway gateway = new CoberturaCoverageGateway()
        gateway.check(checking)

        then: "Cobertura runs with all the given regular expression rates"
        1 * CheckCoverageMain.checkCoverage({
            it[0] == "--regex" &&
                    it[1] == "${checking.regexRateList[0].regex}:${checking.regexRateList[0].rate.branch}:${checking.regexRateList[0].rate.line}" &&
                    it[2] == "--regex" &&
                    it[3] == "${checking.regexRateList[1].regex}:${checking.regexRateList[1].rate.branch}:${checking.regexRateList[1].rate.line}" &&
                    it[4] == "--regex" &&
                    it[5] == "${checking.regexRateList[2].regex}:${checking.regexRateList[2].rate.branch}:${checking.regexRateList[2].rate.line}" &&
                    it[6] == "--regex" &&
                    it[7] == "${checking.regexRateList[3].regex}:${checking.regexRateList[3].rate.branch}:${checking.regexRateList[3].rate.line}"
        })

    }
}
