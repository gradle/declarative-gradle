package org.gradle.api.experimental

import org.gradle.api.*

class KMPPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.add("kmp", KMPApplication::class.java)

        println("KMPPlugin applied to ${project.name}")
    }
}

abstract class KMPApplication {

}