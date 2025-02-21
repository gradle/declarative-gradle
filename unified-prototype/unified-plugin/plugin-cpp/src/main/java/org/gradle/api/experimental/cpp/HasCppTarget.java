package org.gradle.api.experimental.cpp;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface HasCppTarget {
    @Restricted
    Property<String> getCppVersion();
}
