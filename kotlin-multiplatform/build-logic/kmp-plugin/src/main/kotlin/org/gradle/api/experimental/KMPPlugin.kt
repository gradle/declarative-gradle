
package org.gradle.api.experimental

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.artifacts.dsl.GradleDependencies
import org.gradle.api.experimental.internal.VersionCatalogLoader
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers
import org.gradle.api.tasks.Nested

class KMPPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val pluginLibs = VersionCatalogLoader.loadPluginVersionCatalog(project)
        project.plugins.apply(pluginLibs.getPlugin("kotlinMultiplatform").id)

        val kmpExtension = project.extensions.create("kmpApplication", KMPApplicationExtension::class.java)

        println("KMPPlugin applied to ${project.name}")
    }
}

@Suppress("UnstableApiUsage")
abstract class KMPDependencies : PlatformDependencyModifiers, TestFixturesDependencyModifiers, GradleDependencies {
    @get:Nested
    abstract val implementation: DependencyCollector
    @get:Nested
    abstract val compileOnly: DependencyCollector
    @get:Nested
    abstract val runtimeOnly: DependencyCollector
}

abstract class KMPApplicationExtension {
    @get:Nested
    abstract val dependencies: KMPSourceSetDependencies

    @get:Nested
    abstract val sourceSets: KMPSourceSets

    fun dependencies(configure: KMPSourceSetDependencies.() -> Unit) {
        configure(dependencies)
    }

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

    @get:Nested
    abstract val jsMain: KMPSourceSet

    fun jsMain(configure: KMPSourceSet.() -> Unit) {
        configure(jsMain)
    }

    @get:Nested
    abstract val jvmMain: KMPSourceSet

    fun jvmMain(configure: KMPSourceSet.() -> Unit) {
        configure(jvmMain)
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
