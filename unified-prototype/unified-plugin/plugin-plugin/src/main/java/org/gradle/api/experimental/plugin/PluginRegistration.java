package org.gradle.api.experimental.plugin;

import org.gradle.api.Named;
import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.ElementFactoryName;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@ElementFactoryName("id")
public interface PluginRegistration extends Named {
    @Restricted
    Property<String> getDescription();

    @Restricted
    Property<String> getImplementationClass();
}
