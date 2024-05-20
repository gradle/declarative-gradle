package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Extension to manage setting up Jacoco for an Android library.
 */
@Restricted
public interface Jacoco {
    /**
     * Internal property purposely not exposed to the DSL.
     */
    Property<Boolean> getEnabled();

    /**
     * Jacoco tool version to use.
     */
    @Restricted
    Property<String> getVersion();
}
