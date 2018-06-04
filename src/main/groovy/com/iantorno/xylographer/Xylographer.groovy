package com.iantorno.xylographer

import com.iantorno.xylographer.publisher.PublishTask
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Matcher

class Xylographer implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // the meat and potatoes of the plugin
        println "Xylographer has been configured for build versioning!"

        project.task('renameAppVersionName', type: PublishTask)

        /*
         * Since Android build includes versionName info in the app, we must define task dependency in order trigger it
         * before Android build starts. There is a gradle task named preBuild which is perfect to accomplish this
         * requirement.
         */
        project.tasks.getByName('preBuild').dependsOn('renameAppVersionName')
    }
}



