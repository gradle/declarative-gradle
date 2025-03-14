package org.gradle.api.experimental.jvm;

import org.gradle.api.experimental.common.HasApplicationDependencies;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

/**
 * Represents an application that runs on the JVM.
 */
public interface HasJvmApplication extends HasApplicationDependencies {
    @Restricted
    Property<String> getMainClass();

    @Restricted
    ListProperty<String> getJvmArguments();
}
