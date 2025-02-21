package org.gradle.api.experimental.android.extensions;

import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.declarative.dsl.model.annotations.Configuring;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Extension to manage setting up Kotlin serialization for a Kotlin Android library.
 */
@SuppressWarnings("UnstableApiUsage")
public interface KotlinSerialization {
    @Restricted
    Property<Boolean> getEnabled();

    /**
     * Kotlin serialization library version to use.
     */
    @Restricted
    Property<String> getVersion();

    @Nested
    SerializationDependencies getDependencies();

    @Configuring
    default void dependencies(Action<? super SerializationDependencies> action) {
        action.execute(getDependencies());
    }

    @Restricted
    Property<Boolean> getJsonEnabled();

    interface SerializationDependencies extends Dependencies, PlatformDependencyModifiers {
        DependencyCollector getImplementation();
    }
}


