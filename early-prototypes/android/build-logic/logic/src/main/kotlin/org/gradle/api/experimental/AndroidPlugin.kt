package org.gradle.api.experimental

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.*
import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.artifacts.dsl.GradleDependencies
import org.gradle.api.experimental.common.SourceFileCollection
import org.gradle.api.experimental.common.plusAssign
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Make sure we register an afterEvaluate before android gets to.
        // Otherwise, it will complain we are setting values too late.
        project.afterEvaluate {
            val android = project.extensions.getByType(com.android.build.api.dsl.ApplicationExtension::class.java)
            val kotlinExt = project.extensions.getByType(KotlinAndroidProjectExtension::class.java)
            val application = project.extensions.getByType(ConventionalAndroidApplication::class.java)
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)

            kotlinExt.run {
                jvmToolchain(application.jdkVersion.get())
            }
            android.run {
                namespace = application.namespace.get()
                compileSdk = application.compileSdk.get()
                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(application.jdkVersion.get())
                    targetCompatibility = JavaVersion.toVersion(application.jdkVersion.get())
                }

                defaultConfig {
                    // Common configuration
                }

                flavorDimensions += "type"
                productFlavors {
                    create("demo") {
                        dimension = "type"
                        applicationIdSuffix = ".demo"
                    }
                    create("full") {
                        dimension = "type"
                        applicationIdSuffix = ".full"
                    }
                    configureEach {
                        // How does this differ from defaultConfig?
                    }
                }

                project.configurations.run {
                    getByName("implementation").dependencies.addAllLater(application.dependencies.implementation.dependencies)
                    getByName("runtimeOnly").dependencies.addAllLater(application.dependencies.runtimeOnly.dependencies)
                    getByName("compileOnly").dependencies.addAllLater(application.dependencies.compileOnly.dependencies)
                }

                sourceSets {
                    named("main") {
                        kotlin.srcDir(application.sources.kotlin.files)
                    }
                }
            }

            androidComponents.run {
                beforeVariants { androidVariant ->
                    targetsByName(application)[androidVariant.name]?.let { explicitVariant ->
                        androidVariant.minSdk = explicitVariant.minSdk.get()
                    }
                }
                onVariants { androidVariant ->
                    targetsByName(application)[androidVariant.name]?.let { explicitVariant ->
                        project.configurations.run {
                            getByName("${androidVariant.name}Implementation").dependencies.addAllLater(explicitVariant.dependencies.implementation.dependencies)
                            getByName("${androidVariant.name}RuntimeOnly").dependencies.addAllLater(explicitVariant.dependencies.runtimeOnly.dependencies)
                            getByName("${androidVariant.name}CompileOnly").dependencies.addAllLater(explicitVariant.dependencies.compileOnly.dependencies)
                        }
                        android.sourceSets {
                            // TODO: There is a bug in android where src dirs added to
                            // target-specific android source sets are not added to the corresponding
                            // kotlin source set, and are thus not compiled.
                            named(androidVariant.name) {
                                kotlin.srcDir(explicitVariant.sources.kotlin.files)
                            }
                        }
                    }
                }
            }
        }

        project.plugins.apply("com.android.application")
        project.plugins.apply("org.jetbrains.kotlin.android")

        val application = project.extensions.create("androidApplication", ConventionalAndroidApplication::class.java)
        application.jdkVersion.set(17)

        application.targets2.create("demoDebug")
        application.targets2.create("demoRelease")
        application.targets2.create("fullDebug")
        application.targets2.create("fullRelease")

        // Set some default source directories
        application.sources {
            kotlin += project.file("src/main2/kotlin")
            kotlin.encoding.set(Charsets.UTF_8)
        }
        targetsByName(application).forEach {
            it.value.sources {
                kotlin += project.file("src/${it.key}2/kotlin")
                kotlin.encoding.set(Charsets.UTF_8)
            }
        }

        // Link target defaults to application defaults
        targetsByName(application).forEach {
            it.value.minSdk.set(application.minSdk)
        }
    }
}

fun targetsByName(application: ConventionalAndroidApplication) = mapOf(
    "demoDebug" to application.targets.demoDebug,
    "demoRelease" to application.targets.demoRelease,
    "fullDebug" to application.targets.fullDebug,
    "fullRelease" to application.targets.fullRelease
)

abstract class AndroidSources {
    @get:Nested
    abstract val kotlin: SourceFileCollection
}

abstract class ConventionalAndroidApplication {

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val compileSdk: Property<Int>

    @get:Input
    abstract val jdkVersion: Property<Int>

    @get:Input
    abstract val minSdk: Property<Int>

    // Common sources
    @get:Nested
    abstract val sources: AndroidSources

    fun sources(configure: AndroidSources.() -> Unit) {
        configure(sources)
    }

    // Common dependencies
    @get:Nested
    abstract val dependencies: ApplicationDependencies

    fun dependencies(configure: ApplicationDependencies.() -> Unit) {
        configure(dependencies)
    }

    @get:Nested
    abstract val targets: AndroidApplicationTargets

    fun targets(configure: AndroidApplicationTargets.() -> Unit) {
        configure(targets)
    }

    @get:Nested
    abstract val targets2: NamedDomainObjectContainer<NamedAndroidTarget>

    fun targets2(configure: NamedDomainObjectContainer<NamedAndroidTarget>.() -> Unit) {
        configure(targets2)
    }
}

abstract class AndroidApplicationTargets {
    @get:Nested
    abstract val demoDebug: AndroidTarget

    fun demoDebug(configure: AndroidTarget.() -> Unit) {
        configure(demoDebug)
    }

    @get:Nested
    abstract val demoRelease: AndroidTarget

    fun demoRelease(configure: AndroidTarget.() -> Unit) {
        configure(demoRelease)
    }

    @get:Nested
    abstract val fullDebug: AndroidTarget

    fun fullDebug(configure: AndroidTarget.() -> Unit) {
        configure(fullDebug)
    }

    @get:Nested
    abstract val fullRelease: AndroidTarget

    fun fullRelease(configure: AndroidTarget.() -> Unit) {
        configure(fullRelease)
    }
}

abstract class NamedAndroidTarget(private val name: String) : AndroidTarget(), Named {
    override fun getName() = name
}

abstract class AndroidTarget {
    @get:Input
    abstract val minSdk: Property<Int>

    @get:Nested
    abstract val dependencies: ApplicationDependencies

    fun dependencies(configure: ApplicationDependencies.() -> Unit) {
        configure(dependencies)
    }

    @get:Nested
    abstract val sources: AndroidSources

    fun sources(configure: AndroidSources.() -> Unit) {
        configure(sources)
    }
}

abstract class ApplicationDependencies : PlatformDependencyModifiers, TestFixturesDependencyModifiers,
    GradleDependencies {
    @get:Nested
    abstract val implementation: DependencyCollector
    @get:Nested
    abstract val runtimeOnly: DependencyCollector
    @get:Nested
    abstract val compileOnly: DependencyCollector
}
