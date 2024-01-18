package org.gradle.api.experimental

import org.gradle.api.artifacts.dsl.DependencyCollector
import org.gradle.api.artifacts.dsl.GradleDependencies
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers
import org.gradle.api.plugins.jvm.TestFixturesDependencyModifiers
import org.gradle.api.tasks.Nested

@Suppress("UnstableApiUsage")
abstract class KMPDependencies : PlatformDependencyModifiers, TestFixturesDependencyModifiers, GradleDependencies {
    @get:Nested
    abstract val implementation: DependencyCollector
    @get:Nested
    abstract val compileOnly: DependencyCollector
    @get:Nested
    abstract val runtimeOnly: DependencyCollector
}
