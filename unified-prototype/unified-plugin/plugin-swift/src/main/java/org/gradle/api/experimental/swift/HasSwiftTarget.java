package org.gradle.api.experimental.swift;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface HasSwiftTarget {
    @Restricted
    Property<String> getSwiftVersion();
}
