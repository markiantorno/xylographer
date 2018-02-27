package com.iantorno.xylographer.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import javax.annotation.Nonnull
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
            def plugins = project.getPlugins()
            if (plugins.hasPlugin('android') || plugins.hasPlugin('android-library')) {
                android {
                    def versionFile = new File(project.rootDir, 'version.properties')
                    if (versionFile.exists()) {
                        println "versioning file exists"
//                        incrementVersionProperty(versionFile, ReleaseType.VERSION_BUILD)
                    } else {
                        println "versioning file doesn't exist, creating as new"
                        versionFile.createNewFile()
                        initializeVersionFile(versionFile)
                    }
//                    defaultConfig {
//                        versionName = calculateVersionName()
//                        versionCode = calculateVersionCode()
//                    }
//
//                    afterEvaluate {
//                        def autoIncrementVariant = { variant ->
//                            if (variant.buildType.name == buildTypes.release.name) { // don't increment on debug builds
//                                variant.preBuild.dependsOn incrementVersion
//                                incrementVersion.doLast {
//                                    variant.mergedFlavor.versionName = calculateVersionName()
//                                    variant.mergedFlavor.versionCode = calculateVersionCode()
//                                }
//                            }
//                        }

                    Gradle gradle = project.getGradle()
                    String  taskReqStr = gradle.getStartParameter().getTaskRequests().toString()
                    println(taskReqStr)

                    // Iterate over app build variants (build types + flavors)
                    project.android.applicationVariants.all { variant ->

                        println("BUILD TYPE: " + variant.buildType.name)
                        // Only change debug build type variants
//                        if (variant.buildType.name == project.android.buildTypes.debug.name) {
//                            // Rename versionName
//                            def customVersionName = variant.mergedFlavor.versionName
//                            println "version name: ${customVersionName}"
//                            variant.mergedFlavor.versionName = customVersionName + " custom"
//                            println "version name combined: ${variant.mergedFlavor.versionName}"
//                        }


//                        if (plugins.hasPlugin('android')) {
//                            println "has plugin android"
////                            incrementProperty(versionFile, ReleaseType.VERSION_MINOR)
//                            println("Version Number: " + buildVersionNumber(versionFile, ReleaseType.VERSION_BUILD))
////                            applicationVariants.all { variant -> autoIncrementVariant(variant) }
//                        }
//                        if (plugins.hasPlugin('android-library')) {
//                            println "has plugin android-library"
////                            libraryVariants.all { variant -> autoIncrementVariant(variant) }
//                        }
                    }
//                    }
                }
            }

//            // Check if plugin works on an Android module
//            if (it.hasProperty("android")) {
//                // Iterate over app build variants (build types + flavors)
//                project.android.applicationVariants.all { variant ->
//                    // Only change debug build type variants
//                    if (variant.buildType.name == project.android.buildTypes.debug.name) {
//                        // Rename versionName
//                        def customVersionName = variant.mergedFlavor.versionName
//                        println "version name: ${customVersionName}"
//                        variant.mergedFlavor.versionName = customVersionName + " custom"
//                        println "version name combined: ${variant.mergedFlavor.versionName}"
//                    }
//                }
//            }
        }
    }


//    static def renameDebugAppVersionName(variant) {
//        def customVersionName = variant.mergedFlavor.versionName + getCurrentBranchCodeName()
//        variant.mergedFlavor.versionName = customVersionName
//        println "${variant.name} version name: ${customVersionName}"
//    }

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


///////////////////////////////

    /**
     * Initializes version file with all 0 values for each release type.
     * @param file {@link File} to store properties in
     */
    void initializeVersionFile(@Nonnull File file) {
        for (ReleaseType type : ReleaseType.values()) {
             setProperty(file, type as String, 0)
        }
    }

    /**
     * Increments the given property by 1.
     * N.B. This will cascade and reset to 0, all lesser release types.
     *
     * @param propertiesFile {@link File} containing properties data
     * @param type {@link ReleaseType}
     */
    void incrementProperty(@Nonnull File propertiesFile, @Nonnull ReleaseType type) {
        Properties propertiesValues = loadVersionProperties(propertiesFile)

        for (ReleaseType releaseType : ReleaseType.values().reverse()) {
            String property = propertiesValues.getProperty(releaseType as String)
            if(releaseType.equals(type)) {
                if (property == null) {
                    setProperty(propertiesFile, releaseType as String, 1)
                } else {
                    setProperty(propertiesFile, releaseType as String, property.toInteger() + 1)
                }
                break
            } else {
                setProperty(propertiesFile, releaseType as String, 0)
            }
        }
        printCurrentProperties(propertiesValues)
    }

    /**
     * Writes the given key value pair to the properties file.
     *
     * @param propertiesFile {@link File} containing the properties to edit
     * @param key {@link String} key to associate with value
     * @param value {@link Integer} value to store
     */
    void setProperty(@Nonnull File propertiesFile, @Nonnull String key, int value) {
        Properties propertiesValues = loadVersionProperties(propertiesFile)
        propertiesValues.setProperty(key, value.toString())
        propertiesValues.store(propertiesFile.newWriter(), null)
    }

    /**
     * Gets the given key value pair to the properties file.
     *
     * @param propertiesFile {@link File} containing the properties to edit
     * @param key {@link String} key to associate with value
     * @param value {@link Integer} value to store
     */
    int getProperty(@Nonnull File propertiesFile, @Nonnull String key) {
        Properties propertiesValues = loadVersionProperties(propertiesFile)
        def property = propertiesValues.getProperty(key, "0")
        return property.toInteger()
    }

    /**
     * Loads the 'version.properties' file for reading. Throws FileNotFoundException if no such
     * file exists.
     * @return {@link Properties} read from file.
     */
    Properties loadVersionProperties(File file) {
        if (!file.canRead()) {
            throw new FileNotFoundException("Could not read version.properties!")
        }
        Properties versionProps = new Properties()
        versionProps.load(new FileInputStream(file))
        return versionProps
    }

    /**
     * Dumps the current propertis to the console.
     * @param properties {@link Properties} to display in console
     */
    void printCurrentProperties(Properties properties) {
        properties.each { prop, val ->
            println(prop + ": " + val)
        }
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
    String buildVersionCode(@Nonnull File propertiesFile, @Nonnull ReleaseType type) {
        def major = getProperty(propertiesFile, ReleaseType.VERSION_MAJOR as String)
        def minor = getProperty(propertiesFile, ReleaseType.VERSION_MINOR as String)
        def revision = getProperty(propertiesFile, ReleaseType.VERSION_REVISION as String)
        def build = getProperty(propertiesFile, ReleaseType.VERSION_BUILD as String)
        return " "

        //return ((major * 10000000) + (minor * 100000) + (revision * 1000) + (build * 1))
    }

    int buildVersionNumber(@Nonnull File propertiesFile, @Nonnull ReleaseType type) {
        def major = getProperty(propertiesFile, ReleaseType.VERSION_MAJOR as String)
        def minor = getProperty(propertiesFile, ReleaseType.VERSION_MINOR as String)
        def revision = getProperty(propertiesFile, ReleaseType.VERSION_REVISION as String)
        def build = getProperty(propertiesFile, ReleaseType.VERSION_BUILD as String)


        return ((major * 10000000) + (minor * 100000) + (revision * 1000) + (build * 1))
    }
}


