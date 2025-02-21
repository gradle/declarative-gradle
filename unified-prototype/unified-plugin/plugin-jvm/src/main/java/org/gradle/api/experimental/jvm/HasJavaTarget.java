package org.gradle.api.experimental.jvm;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * A component that is built for a single Java version.
 */
public interface HasJavaTarget {
    @Restricted
    Property<Integer> getJavaVersion();
}
