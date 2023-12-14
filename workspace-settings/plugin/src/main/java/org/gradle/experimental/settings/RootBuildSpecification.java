package org.gradle.experimental.settings;

import org.gradle.api.provider.Property;

public interface RootBuildSpecification extends ProjectContainer {
    Property<Boolean> getAutodetect();
}
