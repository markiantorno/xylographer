package com.iantorno.xylographer

import org.gradle.api.Plugin
import org.gradle.api.Project

class Xylographer implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // the meat and potatoes of the plugin
        println "Xylographer has been configured for build versioning!"
    }

    @Override
    Object invokeMethod(String s, Object o) {
        return super.invokeMethod(s, o)
    }
}



