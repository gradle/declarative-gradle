package org.gradle.experimental.settings;

import org.gradle.api.provider.Property;

public interface BuildSpecification {
    Property<String> getName();
}
