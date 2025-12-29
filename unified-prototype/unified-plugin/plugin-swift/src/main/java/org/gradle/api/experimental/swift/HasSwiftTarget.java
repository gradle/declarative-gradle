package org.gradle.api.experimental.swift;

import org.gradle.api.provider.Property;

public interface HasSwiftTarget {
    Property<Integer> getSwiftVersion();
}
