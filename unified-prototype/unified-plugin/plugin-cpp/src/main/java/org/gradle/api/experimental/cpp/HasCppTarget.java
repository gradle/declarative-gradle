package org.gradle.api.experimental.cpp;

import org.gradle.api.provider.Property;

public interface HasCppTarget {
    Property<String> getCppVersion();
}
