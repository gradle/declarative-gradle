package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

@Restricted
public interface KmpJvmTarget extends KmpTarget {
    @Restricted
    Property<String> getJvmTarget();
}
