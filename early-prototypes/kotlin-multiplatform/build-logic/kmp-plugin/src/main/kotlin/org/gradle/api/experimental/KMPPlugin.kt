
package org.gradle.api.experimental

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.experimental.internal.VersionCatalogLoader
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KMPPlugin : Plugin<Project> {
    companion object {
        const val DEFAULT_KOTLIN_VERSION = "1.8"
    }

    override fun apply(project: Project) {
        createKMPApplicationExtension(project)
        applyPlugins(project)

        // Necessary to avoid "The value for extension 'kmpApplication' property 'dependencies.implementation' property 'dependencies' is final and cannot be changed any further."
        // due to how the KotlinDependencyHandler is implemented
        project.afterEvaluate {
            configureKotlinAsKMPApplication(project)
        }
    }

    private fun createKMPApplicationExtension(project: Project): KMPApplicationExtension {
        val kmpExtension = project.extensions.create("kmpApplication", KMPApplicationExtension::class.java)
        kmpExtension.targets.registerBinding(KMPTarget::class.java, KMPTarget::class.java)
        kmpExtension.publishSources.convention(false)
        kmpExtension.languageVersion.convention(DEFAULT_KOTLIN_VERSION)
        return kmpExtension
    }

    private fun applyPlugins(project: Project) {
        val pluginLibs = VersionCatalogLoader.loadPluginVersionCatalog(project)
        project.plugins.apply(pluginLibs.getPlugin("kotlinMultiplatform").id)

        project.plugins.apply("maven-publish")
    }

    private fun configureKotlinAsKMPApplication(project: Project) {
        val kmpExtension = project.extensions.getByType(KMPApplicationExtension::class.java)
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        configureLanguageVersion(kotlin, kmpExtension)
        configureSourcePublishing(kotlin, kmpExtension)
        configureCommonDependencies(kmpExtension, kotlin)

        maybeConfigureJvmTarget(kmpExtension, kotlin)
        maybeConfigureJsTarget(kmpExtension, kotlin)
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

    private fun configureCommonDependencies(
        kmpExtension: KMPApplicationExtension,
        kotlin: KotlinMultiplatformExtension
    ) {
        copyDependencies(kmpExtension.dependencies, kotlin, "commonMain")
    }

    private fun maybeConfigureJvmTarget(
        kmpExtension: KMPApplicationExtension,
        kotlin: KotlinMultiplatformExtension
    ) {
        if (kmpExtension.targets.findByName("jvm") != null) {
            kmpExtension.targets.named("jvm", KMPTarget::class.java).configure {
                kotlin.jvm()
                copyDependencies(dependencies, kotlin, "jvmMain")
            }
        }
    }

    private fun maybeConfigureJsTarget(
        kmpExtension: KMPApplicationExtension,
        kotlin: KotlinMultiplatformExtension
    ) {
        if (kmpExtension.targets.findByName("js") != null) {
            kmpExtension.targets.named("js", KMPTarget::class.java).configure {
                kotlin.js() {
                    browser()
                }
                copyDependencies(dependencies, kotlin, "jsMain")
            }
        }
    }

    private fun copyDependencies(dependencies: KMPDependencies, kotlin: KotlinMultiplatformExtension, sourceSetName: String) {
        @Suppress("UnstableApiUsage")
        dependencies.implementation.dependencies.get().forEach {
            kotlin.sourceSets.getByName(sourceSetName).dependencies {
                implementation(it)
            }
        }
    }
}

