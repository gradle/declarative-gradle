package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.provider.Property;

/**
 * Extension to manage setting up Jacoco for an Android library.
 */
public interface Jacoco {
    Property<Boolean> getEnabled();

    /**
     * Jacoco tool version to use.
     */
    Property<String> getVersion();
}
