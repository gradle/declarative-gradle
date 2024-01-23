package org.gradle.api.experimental.jvm

import org.gradle.api.Project
import org.gradle.api.experimental.jvm.internal.JvmLibraryAccessor

/**
 * Used to access a declarative JVM library from the Kotlin DSL
 */
fun Project.jvmLibrary(configure: JvmLibrary.() -> Unit) {
    extensions.getByType(JvmLibraryAccessor::class.java).access().configure()
}