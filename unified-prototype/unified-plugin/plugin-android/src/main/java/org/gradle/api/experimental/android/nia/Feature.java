package org.gradle.api.experimental.android.nia;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * This type defines a Conventional Now In Android feature project.
 */
@Restricted
public interface Feature {
    @Restricted
    Property<Boolean> getEnabled();
}
