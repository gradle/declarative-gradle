package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface Compose {
    @Restricted
    Property<Boolean> getEnabled();

    // TODO: This should be a file property, and not assume it's a path from the root project
    @Restricted
    Property<String> getStabilityConfigurationFilePath();

    @Restricted
    Property<Boolean> getExperimentalStrongSkipping();
}
