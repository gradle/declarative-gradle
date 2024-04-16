package org.gradle.api.experimental.java;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * An application that is built for a single Java version.
 */
@Restricted
public interface JvmApplication extends HasJavaTarget {
    @Restricted
    Property<String> getMainClass();
}
