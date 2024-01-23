package org.gradle.api.experimental.android

import org.gradle.api.Project
import org.gradle.api.experimental.android.internal.AndroidLibraryAccessor

/**
 * Used to access a declarative Android library from the Kotlin DSL
 */
fun Project.androidLibrary(configure: AndroidLibrary.() -> Unit) {
    extensions.getByType(AndroidLibraryAccessor::class.java).access().configure()
}