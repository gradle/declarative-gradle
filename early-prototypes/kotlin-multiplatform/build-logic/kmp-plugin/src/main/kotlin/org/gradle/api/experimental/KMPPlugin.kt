
package org.gradle.api.experimental

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.artifacts.dsl.GradleDependencies
import org.gradle.api.experimental.internal.VersionCatalogLoader
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KMPPlugin : Plugin<Project> {
    companion object {
        const val DEFAULT_KOTLIN_VERSION = "1.8"
    }

    override fun apply(project: Project) {
        val kmpExtension = project.extensions.create("kmpApplication", KMPApplicationExtension::class.java)
        configureConventions(kmpExtension)

        applyPlugins(project)

        // Necessary to avoid "The value for extension 'kmpApplication' property 'dependencies.implementation' property 'dependencies' is final and cannot be changed any further."
        // due to how the KotlinDependencyHandler is implemented
        project.afterEvaluate {
            configureProjectFromExtension(project, kmpExtension)
        }
    }

    private fun applyPlugins(project: Project) {
        val pluginLibs = VersionCatalogLoader.loadPluginVersionCatalog(project)
        project.plugins.apply(pluginLibs.getPlugin("kotlinMultiplatform").id)

        project.plugins.apply("maven-publish")
    }

    private fun configureConventions(kmpExtension: KMPApplicationExtension) {
        kmpExtension.publishSources.convention(false)
        kmpExtension.languageVersion.convention(DEFAULT_KOTLIN_VERSION)
    }

    @Suppress("UnstableApiUsage")
    private fun configureProjectFromExtension(project: Project, kmpExtension: KMPApplicationExtension) {
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        kotlin.jvm()
        kotlin.js() {
            browser()
        }

        kotlin.sourceSets.getByName("commonMain").dependencies {
            kmpExtension.dependencies.implementation.dependencies.get().forEach {
                implementation(it)
            }
        }

        kotlin.sourceSets.getByName("jsMain").dependencies {
            kmpExtension.sourceSets.jsMain.dependencies.implementation.dependencies.get().forEach {
                implementation(it)
            }
        }

        kotlin.sourceSets.getByName("jvmMain").dependencies {
            kmpExtension.sourceSets.jvmMain.dependencies.implementation.dependencies.get().forEach {
                implementation(it)
            }
        }

        configureLanguageVersion(kotlin, kmpExtension)
        configureSourcePublishing(kotlin, kmpExtension)
    }

    private fun configureSourcePublishing(kotlin: KotlinMultiplatformExtension, kmpExtension: KMPApplicationExtension) {
        kotlin.withSourcesJar(kmpExtension.publishSources.get())
    }

    private fun configureLanguageVersion(
        kotlin: KotlinMultiplatformExtension,
        kmpExtension: KMPApplicationExtension
    ) {
        kotlin.sourceSets.all {
            languageSettings.apply {
                languageVersion = kmpExtension.languageVersion.get()
                apiVersion = kmpExtension.languageVersion.get()
            }
        }
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
    @get:Input
    abstract val languageVersion: Property<String>

    @get:Input
    abstract val publishSources: Property<Boolean>

    @get:Nested
    abstract val dependencies: KMPDependencies

    @get:Nested
    abstract val sourceSets: KMPSourceSets

    fun dependencies(configure: KMPDependencies.() -> Unit) {
        configure(dependencies)
    }

    fun sourceSets(configure: KMPSourceSets.() -> Unit) {
        configure(sourceSets)
    }
}

abstract class KMPSourceSets {
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
    abstract val dependencies: KMPDependencies

    fun dependencies(configure: KMPDependencies.() -> Unit) {
        configure(dependencies)
    }
}
