package org.gradle.api.experimental.plugin;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.ElementFactoryName;

@ElementFactoryName("id")
public interface PluginRegistration extends Named {
    Property<String> getDescription();

    Property<String> getImplementationClass();
}
