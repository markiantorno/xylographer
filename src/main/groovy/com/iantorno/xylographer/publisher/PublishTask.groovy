package com.iantorno.xylographer.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher

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

        project.configure(project) {
            // Check if plugin works on an Android module
            if (it.hasProperty("android")) {
                // Iterate over app build variants (build types + flavors)
                project.android.applicationVariants.all { variant ->
                    // Only change debug build type variants
                    if (variant.buildType.name == project.android.buildTypes.debug.name) {
                        // Rename versionName
                        def customVersionName = variant.mergedFlavor.versionName
                        println "version name: ${customVersionName}"
                        variant.mergedFlavor.versionName = customVersionName + " custom"
                        println "version name combined: ${variant.mergedFlavor.versionName}"
                    }
                }
            }
        }
    }

    static def renameDebugAppVersionName(variant) {
        def customVersionName = variant.mergedFlavor.versionName + getCurrentBranchCodeName()
        variant.mergedFlavor.versionName = customVersionName
        println "${variant.name} version name: ${customVersionName}"
    }

    static def getCurrentBranchCodeName() {
        def currentBranchName = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
        String branchTicketCode = "";

        /*
         * REGEX For branch naming:
         *
         * We want to match our JIRA ticketing pattern, ie: 'BANTA-111', BA-29, ICC-2034'...
         *
         * This regex will parse the string for any instance of a 2-5 capital letter identifier, paired with a number
         * from 1-99999, separated by a '-' character. This is unique for my job's purposes, but could be reformatted to
         * suit different needs.
         *
         *  ^ asserts position at start of the string
         *  Match a single character present in the list below [A-Z]{2,5}
         *  {2,5} Quantifier — Matches between 2 and 5 times, as many times as possible, giving back as needed (greedy)
         *  A-Z a single character in the range between A (index 65) and Z (index 90) (case sensitive)
         *  \- matches the character - literally (case sensitive)
         *  \d{1,5} matches a digit (equal to [0-9])
         *  {1,5} Quantifier — Matches between 1 and 5 times, as many times as possible, giving back as needed (greedy)
         *  $ asserts position at the end of the string, or before the line terminator right at the end of the string (if any)
         */
        Matcher matcher = currentBranchName =~ /^[A-Z]{2,5}\-\d{1,5}$/
        if (matcher.size() > 0) {
            branchTicketCode = matcher[0][1]
        }
        return branchTicketCode
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
