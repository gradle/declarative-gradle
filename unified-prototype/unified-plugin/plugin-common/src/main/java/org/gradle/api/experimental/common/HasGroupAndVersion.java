package org.gradle.api.experimental.common;

import org.gradle.api.provider.Property;

public interface HasGroupAndVersion {
    Property<String> getGroup();

    Property<String> getVersion();
}
