package org.gradle.api.experimental.kmp;

import org.gradle.api.provider.Property;
import org.jetbrains.kotlin.gradle.dsl.JvmTarget;

public interface KmpJvmTarget extends KmpTarget {

    Property<JvmTarget> getJvmTarget();

}
