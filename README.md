# Gradle 'perf' Plugin

[JMH](http://openjdk.java.net/projects/code-tools/jmh/) integration for [Gradle](http://www.gradle.org/).

## Example

```groovy

// add the plugin dependency

buildscript {
    repositories {
        maven { url 'https://dl.bintray.com/tbieniek/maven' }
    }
    dependencies {
        classpath 'gradleperf:gradle-perf:1.1.0'
    }
}

// apply the plugin

apply plugin: 'perf'

// optionally configure the plugin

perf {
    all {
        args = ['-f', '10']
    }
    simple {
        args = ['-f', '1']
    }
}

// optionally change the source set location
// default: 'src/perf/java'

sourceSets {
    perf.java.srcDir 'perf'
}

// optionally declare any benchmark dependencies

dependencies {
    perfCompile 'org.apache.commons:commons-io:1.3.2'
}

...
```

will result in:

```
------------------------------------------------------------
All tasks runnable from root project
------------------------------------------------------------

Benchmark tasks
---------------
perf - Runs the performance benchmarks.
perfSimple - Runs the simple performance benchmarks.

...
```

## License 

gradle-perf is free software, and may be redistributed under the Apache license v2.0. (see [LICENSE](LICENSE))
