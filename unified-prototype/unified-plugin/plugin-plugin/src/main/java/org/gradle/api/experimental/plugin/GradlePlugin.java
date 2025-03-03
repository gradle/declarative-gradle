package org.gradle.api.experimental.plugin;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface GradlePlugin extends Named {
    @Restricted
    Property<String> getId();

    @Restricted
    Property<String> getImplementationClass();
}
