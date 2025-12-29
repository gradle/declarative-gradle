package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;

public interface KmpApplicationJvmTarget extends KmpApplicationTarget {
    Property<Integer> getJdkVersion();

    Property<String> getMainClass();
}
