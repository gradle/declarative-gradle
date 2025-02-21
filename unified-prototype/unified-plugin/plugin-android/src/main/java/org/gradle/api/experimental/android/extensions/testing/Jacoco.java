package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Extension to manage setting up Jacoco for an Android library.
 */
public interface Jacoco {
    @Restricted
    Property<Boolean> getEnabled();

    /**
     * Jacoco tool version to use.
     */
    @Restricted
    Property<String> getVersion();
}
