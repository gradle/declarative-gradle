package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface CoreLibraryDesugaring {
    @Restricted
    Property<Boolean> getEnabled();

    /**
     * The version of the library to use for desugaring.
     */
    @Restricted
    Property<String> getLibVersion();
}
