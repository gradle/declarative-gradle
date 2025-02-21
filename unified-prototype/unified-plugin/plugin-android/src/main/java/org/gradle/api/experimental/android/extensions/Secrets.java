package org.gradle.api.experimental.android.extensions;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface Secrets {
    @Restricted
    Property<Boolean> getEnabled();

    @Restricted
    RegularFileProperty getDefaultPropertiesFile();
}
