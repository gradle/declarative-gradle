package org.gradle.experimental.settings;

import org.gradle.api.provider.Property;

public interface RootProjectSpecification extends ProjectContainer {
    Property<Boolean> getAutodetect();
}
