package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface Secrets {
    @Restricted
    Property<Boolean> getEnabled();

    @Restricted
    Property<String> getDefaultPropertiesFileName();
}
