package gradleperf

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

class PerfPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply('java')

        project.sourceSets {
            perf
        }

        project.dependencies {
            perfCompile 'org.openjdk.jmh:jmh-generator-annprocess:0.9.5'
        }

        project.tasks.create(name: 'perf', type: JavaExec, dependsOn: [project.tasks.classes, project.tasks.perfClasses]) {
            main = 'org.openjdk.jmh.Main'

            classpath += project.sourceSets.main.runtimeClasspath
            classpath += project.sourceSets.perf.runtimeClasspath
        }

        project.gradle.projectsEvaluated {
            // configure IntelliJ IDEA plugin if it exists
            project.extensions.findByName('idea')?.module {
                // declare the 'perf' sources as test sources
                // (gives the source files the same green highlight as the test sources)
                testSourceDirs += project.sourceSets.perf.allSource.srcDirs

                // add perf dependencies to TEST scope
                // (prevents import errors/warnings in the IDE)
                scopes.TEST.plus += [ project.configurations.perfRuntime ]
            }

            // configure Eclipse plugin if it exists
            project.extensions.findByName('eclipse')?.classpath {
                plusConfigurations += project.configurations.perfCompile
            }
        }
    }
}
