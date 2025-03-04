package org.gradle.api.experimental.plugin;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * The public DSL interface for a declarative Gradle Plugin.
 */
public interface JavaGradlePlugin {
    @Restricted
    Property<String> getDescription();

    @Nested
    JavaGradlePluginDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super JavaGradlePluginDependencies> action) {
        action.execute(getDependencies());
    }

    NamedDomainObjectContainer<PluginRegistration> getRegisters();
}
