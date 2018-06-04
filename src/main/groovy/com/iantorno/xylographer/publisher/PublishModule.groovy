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
    }
}
