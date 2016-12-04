# coverage-gradle-plugin
A Gradle plug in to get coverage reports.

## Introduction
This coverage plug in allows to have reports about code coverage of Gradle projects. The current version only supports Cobertura
but the plug in has been develop to allow its extension to use other coverage reporting tools. The current version has been 
tested in Java and Kotlin projects as well as in Android applications and libraries with or without Kotlin code in it. This 
plugin may work with other languages such as Scala or Groovy, yet it hasn't been tested.

##How to use it
If you are using the traditional gradle system you can add the plug in like this

```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.psa:coverage-plugin:1.0"
  }
}

apply plugin: "com.psa.coverage"
```

Alternatively, you could use the new plugin system as follows

```gradle
plugins {
  id "com.psa.coverage" version "1.0"
}
```

##Available tasks
The plug in contains three tasks. For Android projects, it will create the three tasks for each variant but the configuration
for all the variants is the same. 

###Instrument
Performs the instrumentation of the classes. This task has to be performed before running the tests or the coverage won't be
possible. The instrumentation is configurable like this:

```gradle
coverage {
  instrumentation {
    includeClass ".*\\.com.my.package.*"
    excludeClass ".*\\.com.my.package.*"
    
    ignore "ignore1"
    ignore "ignore2"
    
    ignoreMethodAnnotation "BindViews"
    ignoreMethodAnnotation "Provides"
    ignoreTrivial true
  }
}
```

###Report
Generates the reports. It is meant to be run after the tests. You can configure the encoding and the formats that can be used 
which are those supported by Cobertura (HTML and XML). It can be configured like this
```gradle
coverage {
  reporting {
    format 'html'
    format 'xml'
    charset 'utf-8'
  }
}
```

###CoverageCheck
Performs a check of the coverage given the minimum percentages. This taks should be executed once the reports are available. 
Its configuration is made as follows

```gradle
coverage {
    coverageChecking {
        classRate {
            line 26
            branch 10
        }

        packageRate {
            line 12
            branch 11
        }

        totalRate {
            line 26
            branch 1
        }
        
        regexRate {
            regex "*.MG"
            line 26
            branch 85
        }

        regexRate {
            regex"*.ILMG"
            line 14
            branch 2
        }
    }
}
```

##Configuring the plug in
You have seen in each task the parts of the configuration of the plug in which are relevant to each of them. The plug in adds
a new extension called coverage. The idea of the extension is to work as a DSL in the same fashion than the Android plug in 
does. So, the full configuration shown in this file should look like this in your build.gradle.

```gradle
coverage {
  instrumentation {
    includeClass ".*\\.com.my.package.*"
    excludeClass ".*\\.com.my.package.*"
    
    ignore "ignore1"
    ignore "ignore2"
    
    ignoreMethodAnnotation "BindViews"
    ignoreMethodAnnotation "Provides"
    ignoreTrivial true
  }
  
  reporting {
    format 'html'
    format 'xml'
    charset 'utf-8'
  }
  
  coverageChecking {
        classRate {
            line 26
            branch 10
        }

        packageRate {
            line 12
            branch 11
        }

        totalRate {
            line 26
            branch 1
        }
        
        regexRate {
            regex "*.MG"
            line 26
            branch 85
        }

        regexRate {
            regex"*.ILMG"
            line 14
            branch 2
        }
    }
}
```
