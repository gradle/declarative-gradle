package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;

public interface ManagedDevice extends Named {
    Property<String> getDevice();

    Property<Integer> getApiLevel();

    Property<String> getSystemImageSource();
}
