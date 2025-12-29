package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;

public interface KmpLibraryJvmTarget extends KmpLibraryTarget {
    Property<Integer> getJdkVersion();
}
