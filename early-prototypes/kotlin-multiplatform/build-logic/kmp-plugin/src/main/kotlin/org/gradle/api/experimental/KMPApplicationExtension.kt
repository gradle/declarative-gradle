package org.gradle.api.experimental

import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.NamedDomainObjectContainerScope

abstract class KMPApplicationExtension {
    @get: Input
    abstract val platforms: SetProperty<String>

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

    fun NamedDomainObjectContainerScope<KMPTarget>.jvm(action: KMPTarget.() -> Unit) {
        val jvm = maybeCreate("jvm", KMPTarget::class.java)
        action.invoke(jvm)
    }

    fun NamedDomainObjectContainerScope<KMPTarget>.js(action: KMPTarget.() -> Unit) {
        val js = maybeCreate("js", KMPTarget::class.java)
        action.invoke(js)
    }
}
