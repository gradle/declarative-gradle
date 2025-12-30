package org.gradle.api.experimental.plugin;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.internal.plugins.Definition;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.HiddenInDefinition;

/**
 * The public DSL interface for a declarative Gradle Plugin.
 */
public interface JavaGradlePlugin extends Definition<JavaGradlePluginBuildModel> {
    Property<String> getDescription();

    @Nested
    JavaGradlePluginDependencies getDependencies();

    NamedDomainObjectContainer<PluginRegistration> getRegisters();
}
