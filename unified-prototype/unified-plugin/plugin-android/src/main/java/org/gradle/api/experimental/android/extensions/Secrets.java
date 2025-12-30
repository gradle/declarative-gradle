package org.gradle.api.experimental.android.extensions;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public interface Secrets {
    Property<Boolean> getEnabled();

    RegularFileProperty getDefaultPropertiesFile();
}
