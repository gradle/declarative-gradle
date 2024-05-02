package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;
import org.gradle.declarative.dsl.model.annotations.Restricted;

public interface KmpApplicationJvmTarget extends KmpApplicationTarget {
    @Restricted
    Property<Integer> getJdkVersion();

    @Restricted
    Property<String> getMainClass();
}
