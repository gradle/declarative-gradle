package org.gradle.api.experimental.java;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface JavaComponent {
    @Restricted
    Property<Integer> getJavaVersion();
}
