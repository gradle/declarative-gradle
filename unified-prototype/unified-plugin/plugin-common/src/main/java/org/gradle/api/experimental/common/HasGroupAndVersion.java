package org.gradle.api.experimental.common;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface HasGroupAndVersion {
    @Restricted
    Property<String> getGroup();

    @Restricted
    Property<String> getVersion();
}
