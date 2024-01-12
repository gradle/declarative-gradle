
package org.gradle.api.experimental

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.GradleDependencies
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers
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
    abstract val commonMain: KMPSourceSet

    fun commonMain(configure: KMPSourceSet.() -> Unit) {
        configure(commonMain)
    }
}

abstract class KMPSourceSet {
    @get:Nested
    abstract val dependencies: KMPSourceSetDependencies

    fun dependencies(configure: KMPSourceSetDependencies.() -> Unit) {
        configure(dependencies)
    }
}

@Suppress("UnstableApiUsage")
abstract class KMPSourceSetDependencies : PlatformDependencyModifiers, TestFixturesDependencyModifiers, GradleDependencies {
    @get:Nested
    abstract val implementation: DependencyCollector
}
