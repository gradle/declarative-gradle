package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface ManagedDevice extends Named {
    @Restricted
    Property<String> getDevice();

    @Restricted
    Property<Integer> getApiLevel();

    @Restricted
    Property<String> getSystemImageSource();
}
