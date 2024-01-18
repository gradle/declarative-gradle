package org.gradle.api.experimental

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

abstract class KMPApplicationExtension {
    @get:Input
    abstract val languageVersion: Property<String>

    @get:Input
    abstract val publishSources: Property<Boolean>

    @get:Nested
    abstract val dependencies: KMPDependencies

    fun dependencies(configure: KMPDependencies.() -> Unit) {
        configure(dependencies)
    }

    @get:Nested
    abstract val targets: ExtensiblePolymorphicDomainObjectContainer<KMPTarget>
}
