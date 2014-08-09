package gradleperf

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec

public class PerfPlugin implements Plugin<Project> {
    private static final String DEFAULT_TASK_NAME = 'default';

    void apply(Project project) {
        project.plugins.apply('java')

        def tasks = project.container(Task)
        tasks.create(DEFAULT_TASK_NAME)

        project.extensions.perf = tasks

        project.sourceSets {
            perf
        }

        project.dependencies {
            perfCompile project
            perfCompile 'org.openjdk.jmh:jmh-generator-annprocess:0.9.5'
        }

        project.gradle.projectsEvaluated {
            tasks.each { task ->
                def taskName = 'perf', taskDescription

                if (task.name == DEFAULT_TASK_NAME) {
                    taskDescription = 'Runs the performance benchmarks.'
                } else {
                    taskName += task.name[0].toUpperCase() + task.name.substring(1)
                    taskDescription = 'Runs the ' + task.name + ' performance benchmarks.'
                }

                project.tasks.create(name: taskName, type: JavaExec, dependsOn: [project.tasks.classes, project.tasks.perfClasses]) {
                    description = taskDescription
                    main = 'org.openjdk.jmh.Main'
                    classpath = project.sourceSets.perf.runtimeClasspath
                    args = [*args, *task.args]
                }
            }

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

    public static class Task {
        final String name
        List<String> args = []

        Task(String name) {
            this.name = name
        }
    }
}
