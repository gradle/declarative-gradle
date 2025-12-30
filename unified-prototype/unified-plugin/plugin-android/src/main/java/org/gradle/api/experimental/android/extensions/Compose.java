package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;

public interface Compose {
    Property<Boolean> getEnabled();

    // TODO:DG This should be a file property, and not assume it's a path from the root project
    Property<String> getStabilityConfigurationFilePath();

    Property<Boolean> getExperimentalStrongSkipping();
}
