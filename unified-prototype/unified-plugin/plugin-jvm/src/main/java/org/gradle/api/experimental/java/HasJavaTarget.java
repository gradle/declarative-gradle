package org.gradle.api.experimental.java;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A component that is built for a single Java version.
 */
@Restricted
public interface HasJavaTarget {
    @Restricted
    Property<Integer> getJavaVersion();
}
