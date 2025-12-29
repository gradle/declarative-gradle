package org.gradle.api.experimental.jvm;

import org.gradle.api.provider.Property;

/**
 * A component that is built for a single Java version.
 */
public interface HasJavaTarget {
    Property<Integer> getJavaVersion();
}
