package org.gradle.api.experimental.kmp

import org.gradle.api.Project
import org.gradle.api.experimental.kmp.internal.KmpLibraryAccessor

/**
 * Used to access a declarative KMP library from the Kotlin DSL
 */
fun Project.kmpLibrary(configure: KmpLibrary.() -> Unit) {
    extensions.getByType(KmpLibraryAccessor::class.java).access().configure()
}