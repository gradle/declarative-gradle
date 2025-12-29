package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;

public interface CoreLibraryDesugaring {
    Property<Boolean> getEnabled();

    /**
     * The version of the library to use for desugaring.
     */
    Property<String> getLibVersion();
}
