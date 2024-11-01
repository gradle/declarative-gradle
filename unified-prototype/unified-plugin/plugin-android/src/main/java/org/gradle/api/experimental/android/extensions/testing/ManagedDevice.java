package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface ManagedDevice extends Named {
    @Restricted
    abstract Property<String> getDevice();

    @Restricted
    abstract Property<Integer> getApiLevel();

    @Restricted
    abstract Property<String> getSystemImageSource();
}
