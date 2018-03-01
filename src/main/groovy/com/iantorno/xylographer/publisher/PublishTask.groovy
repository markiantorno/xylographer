package com.iantorno.xylographer.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskAction

import javax.annotation.Nonnull
import java.util.regex.Matcher

/**
 *
 */
class PublishTask extends DefaultTask {

    @TaskAction
    void publish() {
        project.configure(project) {
            def plugins = project.getPlugins()
            if (plugins.hasPlugin('android') || plugins.hasPlugin('android-library')) {
                def versionFile = new File(project.rootDir, 'version.properties')
                if (versionFile.exists()) {
                    println "versioning file exists"
                } else {
                    println "versioning file doesn't exist, creating as new"
                    versionFile.createNewFile()
                    initializeVersionFile(versionFile)
                }

                Gradle gradle = project.getGradle()
                ReleaseType releaseType = determineReleaseTypeFromIdString(getBuildIdentifierString(gradle))
                incrementProperty(versionFile, releaseType)

                if (plugins.hasPlugin('android')) {
                    println("This has been identified as a non-library project, and will versioned accordingly...")
                    android.applicationVariants.all { variant ->
                        variant.outputs.all {
                            setVersionCodeOverride(buildVersionCode(versionFile, releaseType))
                            setVersionNameOverride("MARKIANTORNO")
                        }
                    }
                } else if (plugins.hasPlugin('android-library')) {
                    println("This has been identified as a library project, and will versioned accordingly...")
                    android.libraryVariants.all { variant ->
                        variant.outputs.all {
                            //TODO
                        }
                    }
                }
            }
        }
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

///////////////////////////////

    /**
     * Returns the {@link Gradle}.getStartParameter().getTaskRequests()
     * @param gradleInstance {@link Gradle}
     * @return {@link String} build task identifier
     */
    String getBuildIdentifierString(@Nonnull Gradle gradleInstance) {
        String taskReqStr = gradleInstance.getStartParameter().getTaskRequests().toString()
        println("Task identifier for this build task -> " + taskReqStr)
    }

    /**
     * Parses the passed inidString for one of the {ReleaseType#mIdLabel} and returns the associated {@link ReleaseType}
     * @param idString The identifier from {@link Gradle}.getStartParameter().getTaskRequests()
     * @return The associated {@link ReleaseType}, or {@link ReleaseType#VERSION_BUILD} if no match is found
     */
    ReleaseType determineReleaseTypeFromIdString(String idString) {
        ReleaseType returnType = ReleaseType.VERSION_BUILD
        if (idString != null) {
            if (idString.toLowerCase().contains(ReleaseType.VERSION_MAJOR.getIdentifyingLabel().toLowerCase())) {
                returnType = ReleaseType.VERSION_MAJOR
            } else if (idString.toLowerCase().contains(ReleaseType.VERSION_MINOR.getIdentifyingLabel().toLowerCase())) {
                returnType = ReleaseType.VERSION_MINOR
            } else if (idString.toLowerCase().contains(ReleaseType.VERSION_REVISION.getIdentifyingLabel().toLowerCase())) {
                returnType = ReleaseType.VERSION_REVISION
            }
        }
        println("Based on the passed in task id String {" + idString + "}, the rusulting Release type is -> " + returnType)
        return returnType
    }

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
            if (releaseType.equals(type)) {
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
        printCurrentProperties(loadVersionProperties(propertiesFile))
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
    int buildVersionCode(@Nonnull File propertiesFile, @Nonnull ReleaseType type) {
        def major = getProperty(propertiesFile, ReleaseType.VERSION_MAJOR as String)
        def minor = getProperty(propertiesFile, ReleaseType.VERSION_MINOR as String)
        def revision = getProperty(propertiesFile, ReleaseType.VERSION_REVISION as String)
        def build = getProperty(propertiesFile, ReleaseType.VERSION_BUILD as String)

        int versionCode = ((major * 10000000) + (minor * 100000) + (revision * 1000) + (build * 1))
        println("Version code -> " + versionCode)
        return versionCode
    }

    int buildVersionNumber(@Nonnull File propertiesFile, @Nonnull ReleaseType type) {
        def major = getProperty(propertiesFile, ReleaseType.VERSION_MAJOR as String)
        def minor = getProperty(propertiesFile, ReleaseType.VERSION_MINOR as String)
        def revision = getProperty(propertiesFile, ReleaseType.VERSION_REVISION as String)
        def build = getProperty(propertiesFile, ReleaseType.VERSION_BUILD as String)


        return ((major * 10000000) + (minor * 100000) + (revision * 1000) + (build * 1))
    }
}


