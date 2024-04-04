package org.gradle.api.experimental.jvm;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface JavaApplication {
    @Restricted
    Property<Integer> getJavaVersion();

    @Restricted
    Property<String> getMainClass();
}
