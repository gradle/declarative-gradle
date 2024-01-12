package org.gradle.api.experimental

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

class KMPPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val kmpExtension = project.extensions.create("kmpApplication", KMPApplicationExtension::class.java)

        println("KMPPlugin applied to ${project.name}")
    }
}

abstract class KMPApplicationExtension {
    @get:Nested
    abstract val sourceSets: KMPSourceSets

    fun sourceSets(configure: KMPSourceSets.() -> Unit) {
        configure(sourceSets)
    }
}

abstract class KMPSourceSets {
    @get:Nested
    abstract val commonMain: KMPSourceset

    fun commonMain(configure: KMPSourceset.() -> Unit) {
        configure(commonMain)
    }
}

abstract class KMPSourceset {

}
