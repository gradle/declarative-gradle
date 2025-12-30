package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;

public interface Room {
    Property<Boolean> getEnabled();

    Property<String> getSchemaDirectory();

    /**
     * Room libraries version to use.
     */
    Property<String> getVersion();
}
