package org.gradle.api.experimental.android.nia;

import org.gradle.api.provider.Property;

/**
 * This type defines a Conventional Now In Android feature project.
 */
public interface Feature {
    Property<Boolean> getEnabled();
}
