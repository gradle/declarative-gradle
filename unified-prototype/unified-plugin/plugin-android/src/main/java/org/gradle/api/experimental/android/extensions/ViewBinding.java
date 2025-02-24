package org.gradle.api.experimental.android.extensions;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface ViewBinding {
    @Restricted
    Property<Boolean> getEnabled();
}
