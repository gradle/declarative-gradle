package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface Room {
    @Restricted
    Property<Boolean> getEnabled();

    @Restricted
    Property<String> getSchemaDirectory();

    /**
     * Room libraries version to use.
     */
    @Restricted
    Property<String> getVersion();
}
