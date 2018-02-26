package com.iantorno.xylographer.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/**
 * A custom task type, allows projects to create tasks of type 'PublishTask'
 * Reference:
 * https://docs.gradle.org/3.3/userguide/more_about_tasks.html#sec:task_input_output_annotations
 * Example 19.23
 */
class PublishTask extends DefaultTask {

    @Internal
    String message = "Hello"

    @Internal
    String target = "World"

    @TaskAction
    void publish() {
        if (message.toLowerCase(Locale.ROOT).contains("bye")) {
            throw new GradleException("I can't let you do that, Starfox.")
        }

        println "${message}, ${target}!"

        def extension = project.getExtensions().getByName('android')
        extension.conventionMapping.versionName = "1.9.0"

    }


    /**
     * Builds an Android version code from the version of the project.
     * This is designed to handle the -SNAPSHOT and -RC format.
     *
     * I.e. during development the version ends with -SNAPSHOT. As the code stabilizes and release nears
     * one or many Release Candidates are tagged. These all end with "-RC1", "-RC2" etc.
     * And the final release is without any suffix.
     * @return
     */
    @Input
    public static int buildVersionCode(version_code) {
        //The rules is as follows:
        //-SNAPSHOT counts as 0
        //-RC* counts as the RC number, i.e. 1 to 98
        //final release counts as 99.
        //Thus you can only have 98 Release Candidates, which ought to be enough for everyone

        def candidate = "99"
        def (major, minor, patch) = version_code.toLowerCase().replaceAll('-', '').tokenize('.')
        if (patch.endsWith("snapshot")) {
            candidate = "0"
            patch = patch.replaceAll("[^0-9]", "")
        } else {
            def rc
            (patch, rc) = patch.tokenize("rc")
            if (rc) {
                candidate = rc
            }
        }

        (major, minor, patch, candidate) = [major, minor, patch, candidate].collect {
            it.toInteger()
        }

        return ((major * 1000000) + (minor * 10000) + (patch * 100) + candidate)
    }
}
