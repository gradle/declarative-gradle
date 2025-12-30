package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;

public interface Hilt {
    Property<Boolean> getEnabled();
}
