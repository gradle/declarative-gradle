package org.gradle.api.experimental.android.library;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface CoreLibraryDesugaring {
    /**
     * Can be enabled manually, or automatically when the TargetJavaVersion is set to 8 or higher.
     */
    @Restricted
    Property<Boolean> getEnabled();

    /**
     * The version of the library to use for desugaring.
     */
    @Restricted
    Property<String> getLibVersion();
}
