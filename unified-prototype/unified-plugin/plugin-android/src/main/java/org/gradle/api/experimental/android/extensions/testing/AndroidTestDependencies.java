package org.gradle.api.experimental.android.extensions.testing;

import org.gradle.api.artifacts.dsl.Dependencies;
import org.gradle.api.artifacts.dsl.DependencyCollector;
import org.gradle.api.plugins.jvm.PlatformDependencyModifiers;

@SuppressWarnings("UnstableApiUsage")
public interface AndroidTestDependencies extends Dependencies, PlatformDependencyModifiers {
    DependencyCollector getImplementation();
    DependencyCollector getCompileOnly();
    DependencyCollector getRuntimeOnly();
    DependencyCollector getAndroidImplementation();
}
