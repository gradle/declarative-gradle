package org.gradle.api.experimental

import org.gradle.api.Named
import org.gradle.api.tasks.Nested

abstract class KMPTarget(private val name: String) : Named {
    @get:Nested
    abstract val dependencies: KMPDependencies

    fun dependencies(configure: KMPDependencies.() -> Unit) {
        configure(dependencies)
    }

    override fun getName(): String = name
}
