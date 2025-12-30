package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * Represents an application that runs on the JVM.
 */
public interface HasJvmApplication extends HasApplicationDependencies {
    Property<String> getMainClass();

    ListProperty<String> getJvmArguments();
}
