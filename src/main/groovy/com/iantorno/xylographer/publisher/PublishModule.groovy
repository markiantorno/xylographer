package com.iantorno.xylographer.publisher

import org.gradle.api.Project

/**
 * This class helps us group together the tasks and configurations that are related to Publishing.
 * The module's 'load' method is called in the plugin's entry point at {@link com.iantorno.xylographer.Xylographer}
 */
class PublishModule {
    static void load(Project project) {
        /*
        * Register a 'greeting' extension, with the properties defined in GreetingExtension
        * Reference:
        * https://docs.gradle.org/3.5/userguide/custom_plugins.html#sec:getting_input_from_the_build
        * Example 41.2
        */
        project.extensions.create("publish", PublishExtension)

        /*
        * Clever trick so users don't have to reference a custom task class by its fully qualified name.
        * Reference:
        * https://discuss.gradle.org/t/how-to-create-custom-gradle-task-type-and-not-have-to-specify-full-path-to-type-in-build-gradle/6059/4
        */
        project.ext.PublishTask = PublishTask

        /*
        * A task that uses an extension for configuration.
        * Reference:
        * https://docs.gradle.org/3.5/userguide/custom_plugins.html#sec:getting_input_from_the_build
        * Example 41.2
        */
        project.task('debugPublish') {
            group = "Publish"
            description = "Greets the world. Greeting configured in the 'greeting' extension."

            doLast {
                String greeting = project.extensions.greeting.alternativeGreeting ?: "Hello"
                pritnln "debugPublish called"
                println "$greeting, world!"
            }
        }

        /*
        * A task using a project property for configuration.
        * Reference:
        * https://docs.gradle.org/3.5/userguide/build_environment.html#sec:gradle_properties_and_system_properties
        * Example 12.1
        */
        project.task('releasePublish') {
            group = "Publish"
            description = "Greets the user. Target configured through properties."

            doLast {
                String target = project.findProperty("target") ?: "user"
                println "releasePublish called"
                println "Hello, $target!"
            }
        }
    }
}
