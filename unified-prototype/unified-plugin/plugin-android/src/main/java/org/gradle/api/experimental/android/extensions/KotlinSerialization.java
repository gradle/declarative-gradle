package org.gradle.api.experimental.android.extensions;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

/**
 * Extension to manage setting up Kotlin serialization for a Kotlin Android library.
 */
@SuppressWarnings("UnstableApiUsage")
public interface KotlinSerialization {
    Property<Boolean> getEnabled();

    /**
     * Kotlin serialization library version to use.
     */
    Property<String> getVersion();

    @Nested
    SerializationDependencies getDependencies();

    Property<Boolean> getJsonEnabled();

    interface SerializationDependencies extends Dependencies, PlatformDependencyModifiers {
        DependencyCollector getImplementation();
    }
}


